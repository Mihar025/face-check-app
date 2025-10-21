import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:share_plus/share_plus.dart';

import '../../../../models/daily_finance_info.dart';
import '../../../../models/finance_info_response.dart';
import '../../../../services/pdf_service.dart';
import '../../../../services/ApiService.dart';
import '../../../../providers/localization_provider.dart';

/// Глобальная константа основного цвета (видна во всех виджетах файла)
const Color kFinancePrimary = Color(0xFF2D3748);

class FinanceScreen extends StatefulWidget {
  const FinanceScreen({super.key});

  @override
  State<FinanceScreen> createState() => _FinanceScreenState();
}

class _FinanceScreenState extends State<FinanceScreen> with TickerProviderStateMixin {
  late final ApiService _apiService;
  late final AnimationController _fadeController;

  // ValueNotifiers
  late final ValueNotifier<DateTime> _currentWeekStart;
  late final ValueNotifier<FinanceInfoResponse?> _financeInfo;
  late final ValueNotifier<bool> _isLoading;

  // Screen metrics cache
  late Size _screenSize;
  late bool _isSmall;
  late bool _isMedium;
  late bool _isWide;

  // Цвета
  static const Color _bgLight = Color(0xFFF7FAFC);
  static const Color _bgDark = Color(0xFF1A202C);
  static const Color _error = Color(0xFFE53E3E);
  static const Color _success = Color(0xFF48BB78);

  @override
  void initState() {
    super.initState();
    _apiService = ApiService.instance;

    _fadeController = AnimationController(
      duration: const Duration(milliseconds: 450),
      vsync: this,
    );

    final now = DateTime.now();
    final start = now.subtract(Duration(days: now.weekday % 7));
    _currentWeekStart = ValueNotifier<DateTime>(DateTime(start.year, start.month, start.day));
    _financeInfo = ValueNotifier<FinanceInfoResponse?>(null);
    _isLoading = ValueNotifier<bool>(false);

    fetchFinanceInfo();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _updateMetrics();
  }

  void _updateMetrics() {
    final size = MediaQuery.of(context).size;
    _screenSize = size;
    final w = size.width;
    _isSmall = w < 380;
    _isMedium = w >= 380 && w < 900;
    _isWide = w >= 900;
  }

  @override
  void dispose() {
    _fadeController.dispose();
    _currentWeekStart.dispose();
    _financeInfo.dispose();
    _isLoading.dispose();
    super.dispose();
  }

  Future<void> fetchFinanceInfo() async {
    _isLoading.value = true;
    try {
      final response = await _apiService.getFinanceInfo(_currentWeekStart.value);
      if (!mounted) return;
      _financeInfo.value = response;
      _fadeController.forward(from: 0);
    } catch (e) {
      if (!mounted) return;
      final l10n = context.read<LocalizationProvider>().localizations;
      _showSnack('${l10n.get('finance.errorLoadingData')}: $e', isError: true);
    } finally {
      if (mounted) _isLoading.value = false;
    }
  }

  void changeWeek(int days) {
    if (!kIsWeb) HapticFeedback.lightImpact();
    _currentWeekStart.value = _currentWeekStart.value.add(Duration(days: days));
    fetchFinanceInfo();
  }

  String $$(double amount) => '\$${amount.toStringAsFixed(2)}';

  void _showSnack(String message, {bool isError = false}) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            Icon(isError ? Icons.error_outline : Icons.check_circle_outline, color: Colors.white),
            const SizedBox(width: 12),
            Expanded(child: Text(message)),
          ],
        ),
        backgroundColor: isError ? _error : _success,
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        margin: const EdgeInsets.all(20),
        elevation: 8,
      ),
    );
  }

  Future<void> _downloadFinanceReport() async {
    final info = _financeInfo.value;
    if (info == null) return;
    final l10n = context.read<LocalizationProvider>().localizations;
    if (!kIsWeb) HapticFeedback.mediumImpact();

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => const _ProgressDialog(title: 'Preparing PDF...'),
    );

    try {
      await FinancePdfService.generateFinanceReport(info);
      if (!mounted) return;
      Navigator.of(context).pop();
      _showSnack(l10n.get('finance.reportDownloadedSuccessfully'));
    } catch (e) {
      if (!mounted) return;
      Navigator.of(context).pop();
      _showSnack('${l10n.get('finance.errorDownloadingReport')}: $e', isError: true);
    }
  }

  @override
  Widget build(BuildContext context) {
    _updateMetrics();
    final l10n = context.read<LocalizationProvider>().localizations;
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    SystemChrome.setSystemUIOverlayStyle(
      SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: isDark ? Brightness.light : Brightness.dark,
        statusBarBrightness: isDark ? Brightness.dark : Brightness.light,
      ),
    );

    final media = MediaQuery.of(context);
    final clampedTextScale = media.textScaleFactor.clamp(0.9, 1.25);

    return MediaQuery(
      data: media.copyWith(textScaleFactor: clampedTextScale),
      child: ScrollConfiguration(
        behavior: const _NoGlowBehavior(),
        child: Scaffold(
          backgroundColor: isDark ? _bgDark : _bgLight,
          body: ValueListenableBuilder<DateTime>(
            valueListenable: _currentWeekStart,
            builder: (context, weekStart, _) {
              final periodText =
                  '${DateFormat('MM/dd/yy').format(weekStart)} - ${DateFormat('MM/dd/yy').format(weekStart.add(const Duration(days: 6)))}';

              return RefreshIndicator(
                onRefresh: fetchFinanceInfo,
                color: kFinancePrimary,
                child: CustomScrollView(
                  physics: const AlwaysScrollableScrollPhysics(parent: BouncingScrollPhysics()),
                  slivers: [
                    SliverAppBar(
                      expandedHeight: _isWide ? 160 : 140,
                      pinned: true,
                      elevation: 0,
                      backgroundColor: isDark ? _bgDark : Colors.white,
                      iconTheme: IconThemeData(
                        color: isDark ? Colors.white : kFinancePrimary,
                      ),
                      systemOverlayStyle: SystemUiOverlayStyle(
                        statusBarColor: Colors.transparent,
                        statusBarIconBrightness: isDark ? Brightness.light : Brightness.dark,
                        statusBarBrightness: isDark ? Brightness.dark : Brightness.light,
                      ),
                      flexibleSpace: FlexibleSpaceBar(
                        background: SafeArea(
                          bottom: false,
                          child: Padding(
                            padding: EdgeInsets.symmetric(horizontal: _isWide ? 24 : 16),
                            child: Column(
                              children: [
                                const SizedBox(height: 8),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    const SizedBox(width: 48),
                                    const Spacer(),
                                    Row(
                                      children: [
                                        _actionBtn(isDark, Icons.download_rounded, _downloadFinanceReport, l10n.get('finance.downloadTooltip')),
                                        const SizedBox(width: 8),
                                        _actionBtn(isDark, Icons.share_rounded, () {
                                          if (!kIsWeb) HapticFeedback.lightImpact();
                                          final info = _financeInfo.value;
                                          if (info == null) return;
                                          Share.share(
                                            '${l10n.get('finance.shareTitle')} ($periodText):\n'
                                                '${l10n.get('finance.totalHours')}: ${info.totalHoursWorked.toStringAsFixed(1)}\n'
                                                '${l10n.get('finance.totalGrossPay')}: ${$$(info.totalGrossPay)}',
                                          );
                                        }, l10n.get('finance.shareTooltip')),
                                      ],
                                    ),
                                  ],
                                ),
                                Expanded(
                                  child: Column(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      Text(
                                        l10n.get('finance.title'),
                                        textAlign: TextAlign.center,
                                        style: TextStyle(
                                          color: isDark ? Colors.white : kFinancePrimary,
                                          fontSize: _isWide ? 36 : 32,
                                          fontWeight: FontWeight.w800,
                                          letterSpacing: -0.5,
                                        ),
                                      ),
                                      const SizedBox(height: 8),
                                      Text(
                                        periodText,
                                        style: TextStyle(
                                          color: isDark ? Colors.grey[400] : Colors.grey[600],
                                          fontSize: _isWide ? 16 : 15,
                                          fontWeight: FontWeight.w600,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                    ),

                    SliverToBoxAdapter(
                      child: Padding(
                        padding: EdgeInsets.fromLTRB(_isWide ? 24 : 16, 16, _isWide ? 24 : 16, 8),
                        child: _weekNav(isDark, l10n, weekStart),
                      ),
                    ),

                    ValueListenableBuilder<bool>(
                      valueListenable: _isLoading,
                      builder: (_, isLoading, __) {
                        return ValueListenableBuilder<FinanceInfoResponse?>(
                          valueListenable: _financeInfo,
                          builder: (_, info, __) {
                            if (isLoading || info == null) {
                              return const SliverToBoxAdapter(child: SizedBox.shrink());
                            }
                            final cards = <_SummaryCardData>[
                              _SummaryCardData(
                                label: l10n.get('finance.hours'),
                                value: info.totalHoursWorked.toStringAsFixed(1),
                                icon: Icons.schedule_rounded,
                                colors: const [Color(0xFF4A5568), kFinancePrimary],
                              ),
                              _SummaryCardData(
                                label: l10n.get('finance.gross'),
                                value: $$(info.totalGrossPay),
                                icon: Icons.trending_up_rounded,
                                colors: const [Color(0xFF48BB78), Color(0xFF38A169)],
                              ),
                            ];

                            final crossAxisCount = _isWide ? 4 : (_isMedium ? 3 : 2);
                            final spacing = _isWide ? 16.0 : 12.0;

                            return SliverPadding(
                              padding: EdgeInsets.symmetric(horizontal: _isWide ? 24 : 16),
                              sliver: SliverGrid(
                                delegate: SliverChildBuilderDelegate(
                                      (context, i) => _summaryCard(cards[i]),
                                  childCount: cards.length,
                                ),
                                gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                                  crossAxisCount: crossAxisCount,
                                  crossAxisSpacing: spacing,
                                  mainAxisSpacing: spacing,
                                  childAspectRatio: _isWide ? 1.45 : (_isSmall ? 0.85 : 1.0),
                                ),
                              ),
                            );
                          },
                        );
                      },
                    ),

                    SliverToBoxAdapter(
                      child: Padding(
                        padding: EdgeInsets.fromLTRB(_isWide ? 24 : 16, 16, _isWide ? 24 : 16, 8),
                        child: Text(
                          l10n.get('finance.dailyBreakdown'),
                          style: TextStyle(
                            color: isDark ? Colors.white : Colors.black87,
                            fontSize: _isWide ? 22 : 20,
                            fontWeight: FontWeight.w800,
                            letterSpacing: -0.3,
                          ),
                        ),
                      ),
                    ),

                    ValueListenableBuilder<bool>(
                      valueListenable: _isLoading,
                      builder: (_, isLoading, __) {
                        if (isLoading) {
                          return SliverFillRemaining(
                            hasScrollBody: false,
                            child: Center(
                              child: Container(
                                width: 80,
                                height: 80,
                                decoration: BoxDecoration(
                                  color: kFinancePrimary,
                                  borderRadius: BorderRadius.circular(20),
                                  boxShadow: [
                                    BoxShadow(
                                      color: kFinancePrimary.withOpacity(0.2),
                                      blurRadius: 20,
                                      offset: const Offset(0, 10),
                                    ),
                                  ],
                                ),
                                child: const Center(
                                  child: CircularProgressIndicator(color: Colors.white, strokeWidth: 3),
                                ),
                              ),
                            ),
                          );
                        }

                        return ValueListenableBuilder<FinanceInfoResponse?>(
                          valueListenable: _financeInfo,
                          builder: (_, info, __) {
                            final items = info?.dailyInfo ?? const <DailyFinanceInfo>[];
                            if (items.isEmpty) {
                              return SliverFillRemaining(
                                hasScrollBody: false,
                                child: _EmptyState(
                                  title: l10n.get('finance.noDataForPeriod'),
                                  onRefresh: fetchFinanceInfo,
                                ),
                              );
                            }

                            return SliverList(
                              delegate: SliverChildBuilderDelegate(
                                    (context, index) {
                                  if (index == items.length) {
                                    return Padding(
                                      padding: EdgeInsets.fromLTRB(_isWide ? 24 : 16, 16, _isWide ? 24 : 16, 16),
                                      child: _totalsCard(isDark, l10n, info!),
                                    );
                                  }
                                  final daily = items[index];
                                  return Padding(
                                    padding: EdgeInsets.symmetric(horizontal: _isWide ? 24 : 16, vertical: 6),
                                    child: FadeTransition(
                                      opacity: CurvedAnimation(
                                        parent: _fadeController,
                                        curve: Interval(0.05 * index, 0.35 + 0.05 * index, curve: Curves.easeOut),
                                      ),
                                      child: _dailyCard(isDark, daily),
                                    ),
                                  );
                                },
                                childCount: items.length + 1,
                              ),
                            );
                          },
                        );
                      },
                    ),

                    const SliverToBoxAdapter(child: SizedBox(height: 96)),
                  ],
                ),
              );
            },
          ),
        ),
      ),
    );
  }

  Widget _actionBtn(bool isDark, IconData icon, VoidCallback onTap, String tooltip) {
    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Tooltip(
          message: tooltip,
          child: Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: isDark ? Colors.white.withOpacity(0.1) : Colors.grey[100],
              borderRadius: BorderRadius.circular(12),
              border: Border.all(
                color: isDark ? Colors.white.withOpacity(0.2) : Colors.grey[300]!,
                width: 1,
              ),
            ),
            child: Icon(icon, color: isDark ? Colors.white : kFinancePrimary, size: 22),
          ),
        ),
      ),
    );
  }

  Widget _weekNav(bool isDark, dynamic l10n, DateTime currentWeekStart) {
    final subtitleStyle = TextStyle(
      fontSize: _isWide ? 13 : 12,
      color: isDark ? Colors.grey.shade400 : Colors.grey.shade700,
      fontWeight: FontWeight.w600,
    );

    return Container(
      padding: const EdgeInsets.all(2),
      decoration: BoxDecoration(
        color: isDark ? Colors.grey[900] : Colors.white,
        borderRadius: BorderRadius.circular(14),
        boxShadow: isDark
            ? []
            : [BoxShadow(color: Colors.grey.withOpacity(0.08), blurRadius: 8, offset: const Offset(0, 3))],
        border: isDark ? Border.all(color: Colors.white.withOpacity(0.1)) : null,
      ),
      child: Row(
        children: [
          Expanded(
            flex: 2,
            child: _pillButton(
              isDark,
              onTap: () => changeWeek(-7),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.chevron_left_rounded, color: isDark ? Colors.white : kFinancePrimary, size: 22),
                  Text(l10n.get('finance.previous'), style: TextStyle(color: isDark ? Colors.white : kFinancePrimary, fontWeight: FontWeight.w700)),
                ],
              ),
            ),
          ),
          Expanded(
            flex: 3,
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 8),
              child: Column(
                children: [
                  Text(l10n.get('finance.weekPeriod').toUpperCase(),
                      style: TextStyle(fontSize: 10, color: isDark ? Colors.grey.shade600 : Colors.grey.shade500, fontWeight: FontWeight.w700, letterSpacing: 0.8)),
                  const SizedBox(height: 6),
                  Text(DateFormat('MMM d').format(currentWeekStart),
                      style: TextStyle(fontSize: 18, fontWeight: FontWeight.w800, color: isDark ? Colors.white : kFinancePrimary)),
                  Text('to ${DateFormat('MMM d, yyyy').format(currentWeekStart.add(const Duration(days: 6)))}', style: subtitleStyle),
                ],
              ),
            ),
          ),
          Expanded(
            flex: 2,
            child: _pillButton(
              isDark,
              onTap: () => changeWeek(7),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(l10n.get('finance.next'), style: TextStyle(color: isDark ? Colors.white : kFinancePrimary, fontWeight: FontWeight.w700)),
                  Icon(Icons.chevron_right_rounded, color: isDark ? Colors.white : kFinancePrimary, size: 22),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _pillButton(bool isDark, {required VoidCallback onTap, required Widget child}) {
    return Material(
      color: Colors.transparent,
      borderRadius: BorderRadius.circular(12),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 14),
          decoration: BoxDecoration(
            color: isDark ? Colors.white.withOpacity(0.05) : kFinancePrimary.withOpacity(0.08),
            borderRadius: BorderRadius.circular(12),
          ),
          child: child,
        ),
      ),
    );
  }

  Widget _summaryCard(_SummaryCardData data) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final h = constraints.maxHeight;

        if (h < 104) {
          final pad = 8.0;
          final iconSize = 22.0;
          final labelSize = 10.5;
          final valueSize = 16.5;

          return Container(
            decoration: BoxDecoration(
              gradient: LinearGradient(colors: data.colors, begin: Alignment.topLeft, end: Alignment.bottomRight),
              borderRadius: BorderRadius.circular(16),
              boxShadow: [BoxShadow(color: data.colors.first.withOpacity(0.22), blurRadius: 10, offset: const Offset(0, 5))],
            ),
            child: Material(
              color: Colors.transparent,
              borderRadius: BorderRadius.circular(16),
              child: InkWell(
                onTap: () => !kIsWeb ? HapticFeedback.lightImpact() : null,
                borderRadius: BorderRadius.circular(16),
                child: Padding(
                  padding: EdgeInsets.all(pad),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Container(
                        width: iconSize + 10,
                        height: iconSize + 10,
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.2),
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: Icon(data.icon, color: Colors.white, size: iconSize),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text(
                              data.label,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              style: TextStyle(
                                color: Colors.white.withOpacity(0.9),
                                fontSize: labelSize,
                                fontWeight: FontWeight.w600,
                                letterSpacing: 0.4,
                              ),
                            ),
                            const SizedBox(height: 2),
                            FittedBox(
                              fit: BoxFit.scaleDown,
                              alignment: Alignment.centerLeft,
                              child: Text(
                                data.value,
                                style: TextStyle(
                                  color: Colors.white,
                                  fontSize: valueSize,
                                  fontWeight: FontWeight.w800,
                                  letterSpacing: -0.4,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          );
        }

        final pad = (h * 0.12).clamp(8.0, 14.0);
        final contentH = h - 2 * pad;

        final iconBox = (contentH * 0.42).clamp(28.0, 50.0);
        final labelSize = (contentH * 0.18).clamp(10.0, 12.0);
        final valueSize = (contentH * 0.34).clamp(17.0, 22.0);

        return Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(colors: data.colors, begin: Alignment.topLeft, end: Alignment.bottomRight),
            borderRadius: BorderRadius.circular(20),
            boxShadow: [BoxShadow(color: data.colors.first.withOpacity(0.25), blurRadius: 12, offset: const Offset(0, 6))],
          ),
          child: Material(
            color: Colors.transparent,
            borderRadius: BorderRadius.circular(20),
            child: InkWell(
              onTap: () => !kIsWeb ? HapticFeedback.lightImpact() : null,
              borderRadius: BorderRadius.circular(20),
              child: Padding(
                padding: EdgeInsets.all(pad),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      height: iconBox,
                      width: iconBox,
                      decoration: BoxDecoration(
                        color: Colors.white.withOpacity(0.2),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Icon(data.icon, color: Colors.white, size: iconBox * 0.55),
                    ),
                    const Spacer(),
                    Text(
                      data.label,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: TextStyle(
                        color: Colors.white.withOpacity(0.9),
                        fontSize: labelSize,
                        fontWeight: FontWeight.w600,
                        letterSpacing: 0.5,
                      ),
                    ),
                    const SizedBox(height: 2),
                    FittedBox(
                      fit: BoxFit.scaleDown,
                      alignment: Alignment.centerLeft,
                      child: Text(
                        data.value,
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: valueSize,
                          fontWeight: FontWeight.w800,
                          letterSpacing: -0.5,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _dailyCard(bool isDark, DailyFinanceInfo daily) {
    final hasData = daily.hoursWorked > 0;
    final badgeColor = hasData ? kFinancePrimary : (isDark ? Colors.grey.shade800 : Colors.grey.shade200);
    final textColor = hasData ? Colors.white : (isDark ? Colors.grey.shade500 : Colors.grey.shade600);

    return Container(
      decoration: BoxDecoration(
        color: isDark ? Colors.grey[900] : Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: hasData && !isDark ? [BoxShadow(color: Colors.grey.withOpacity(0.06), blurRadius: 8, offset: const Offset(0, 3))] : [],
        border: Border.all(
          color: hasData ? Colors.transparent : (isDark ? Colors.white.withOpacity(0.1) : Colors.grey.shade200),
          width: 1,
        ),
      ),
      child: Material(
        color: Colors.transparent,
        borderRadius: BorderRadius.circular(16),
        child: InkWell(
          onTap: hasData ? () => !kIsWeb ? HapticFeedback.lightImpact() : null : null,
          borderRadius: BorderRadius.circular(16),
          child: Padding(
            padding: EdgeInsets.all(_isWide ? 18 : 16),
            child: Row(
              children: [
                Container(
                  width: _isWide ? 68 : 60,
                  height: _isWide ? 68 : 60,
                  decoration: BoxDecoration(color: badgeColor, borderRadius: BorderRadius.circular(12)),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(DateFormat('EEE').format(daily.date).toUpperCase(),
                          style: TextStyle(color: textColor, fontSize: 11, fontWeight: FontWeight.w800, letterSpacing: 0.5)),
                      const SizedBox(height: 2),
                      Text(DateFormat('dd').format(daily.date),
                          style: TextStyle(color: textColor, fontSize: _isWide ? 22 : 20, fontWeight: FontWeight.w800)),
                    ],
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      _dataItem(isDark, Icons.schedule, '${daily.hoursWorked.toStringAsFixed(1)}h', hasData),
                      _dataItem(isDark, Icons.attach_money, $$(daily.grossPay), hasData),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _dataItem(bool isDark, IconData icon, String value, bool hasData) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 18, color: hasData ? kFinancePrimary : (isDark ? Colors.grey.shade700 : Colors.grey.shade400)),
        const SizedBox(height: 4),
        Text(
          value,
          style: TextStyle(
            fontSize: _isWide ? 15 : 14,
            fontWeight: hasData ? FontWeight.w700 : FontWeight.w500,
            color: hasData ? (isDark ? Colors.white : Colors.grey.shade800) : (isDark ? Colors.grey.shade600 : Colors.grey.shade400),
          ),
        ),
      ],
    );
  }

  Widget _totalsCard(bool isDark, dynamic l10n, FinanceInfoResponse info) {
    final divider = Container(width: 1, height: 40, color: Colors.white.withOpacity(0.2));
    return Container(
      decoration: BoxDecoration(
        gradient: const LinearGradient(colors: [kFinancePrimary, Color(0xFF4A5568)], begin: Alignment.topLeft, end: Alignment.bottomRight),
        borderRadius: BorderRadius.circular(20),
        boxShadow: [BoxShadow(color: kFinancePrimary.withOpacity(0.2), blurRadius: 20, offset: const Offset(0, 10))],
      ),
      child: Padding(
        padding: EdgeInsets.all(_isWide ? 22 : 20),
        child: Column(
          children: [
            Row(mainAxisAlignment: MainAxisAlignment.center, children: [
              Icon(Icons.assessment_rounded, color: Colors.white.withOpacity(0.9), size: 20),
              const SizedBox(width: 8),
              Text(l10n.get('finance.totals').toUpperCase(),
                  style: TextStyle(color: Colors.white.withOpacity(0.9), fontSize: 12, fontWeight: FontWeight.w800, letterSpacing: 1.5)),
            ]),
            const SizedBox(height: 18),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _totalItem(Icons.schedule_rounded, info.totalHoursWorked.toStringAsFixed(1), l10n.get('finance.hours')),
                divider,
                _totalItem(Icons.trending_up_rounded, $$(info.totalGrossPay), l10n.get('finance.gross')),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _totalItem(IconData icon, String value, String label) {
    return Column(
      children: [
        Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(color: Colors.white.withOpacity(0.15), shape: BoxShape.circle),
          child: Icon(icon, color: Colors.white, size: 18),
        ),
        const SizedBox(height: 8),
        Text(value, style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.w800, letterSpacing: -0.5)),
        Text(label, style: TextStyle(color: Colors.white.withOpacity(0.85), fontSize: 11, letterSpacing: 0.4)),
      ],
    );
  }
}

class _SummaryCardData {
  final String label;
  final String value;
  final IconData icon;
  final List<Color> colors;
  const _SummaryCardData({required this.label, required this.value, required this.icon, required this.colors});
}

class _NoGlowBehavior extends ScrollBehavior {
  const _NoGlowBehavior();
  @override
  Widget buildOverscrollIndicator(BuildContext context, Widget child, ScrollableDetails details) => child;
}

class _ProgressDialog extends StatelessWidget {
  final String title;
  const _ProgressDialog({required this.title});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Dialog(
      insetPadding: const EdgeInsets.symmetric(horizontal: 48),
      backgroundColor: Colors.transparent,
      child: Container(
        padding: const EdgeInsets.all(24),
        decoration: BoxDecoration(
          color: isDark ? const Color(0xFF2D3748) : Colors.white,
          borderRadius: BorderRadius.circular(20),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.08), blurRadius: 18, offset: const Offset(0, 8))],
        ),
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          Container(
            width: 60,
            height: 60,
            decoration: BoxDecoration(color: kFinancePrimary, borderRadius: BorderRadius.circular(15)),
            child: const Center(child: CircularProgressIndicator(color: Colors.white, strokeWidth: 3)),
          ),
          const SizedBox(height: 18),
          Text(
            title,
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.w700,
              color: isDark ? Colors.white : Colors.black87,
            ),
          ),
        ]),
      ),
    );
  }
}

class _EmptyState extends StatelessWidget {
  final String title;
  final VoidCallback onRefresh;
  const _EmptyState({required this.title, required this.onRefresh});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
          Container(
            width: 110,
            height: 110,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: isDark
                    ? [Colors.grey.shade800, Colors.grey.shade700]
                    : [Colors.grey.shade300, Colors.grey.shade400],
              ),
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.event_busy_rounded, size: 54, color: Colors.white),
          ),
          const SizedBox(height: 22),
          Text(
            title,
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 18,
              color: isDark ? Colors.grey.shade400 : Colors.grey.shade700,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 14),
          ElevatedButton.icon(
            onPressed: onRefresh,
            icon: const Icon(Icons.refresh_rounded),
            label: const Text('Refresh'),
            style: ElevatedButton.styleFrom(
              backgroundColor: kFinancePrimary,
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 28, vertical: 14),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              elevation: 0,
            ),
          ),
        ]),
      ),
    );
  }
}