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

class FinanceScreen extends StatefulWidget {
  const FinanceScreen({super.key});

  @override
  State<FinanceScreen> createState() => _FinanceScreenState();
}

class _FinanceScreenState extends State<FinanceScreen> with TickerProviderStateMixin {
  late ApiService _apiService;
  late AnimationController _animationController;
  late AnimationController _slideController;

  // ValueNotifiers для оптимизации перерисовок
  late final ValueNotifier<DateTime> _currentWeekStart;
  late final ValueNotifier<FinanceInfoResponse?> _financeInfo;
  late final ValueNotifier<bool> _isLoading;

  // Кэшированные значения MediaQuery
  late Size _screenSize;
  late bool _isSmallScreen;

  // Константы для производительности
  static const double _smallScreenThreshold = 360.0;
  static const Color _primaryColor = Color(0xFF2D3748);
  static const Color _backgroundColor = Color(0xFFF7FAFC);
  static const Color _errorColor = Color(0xFFE53E3E);
  static const Color _successColor = Color(0xFF48BB78);

  @override
  void initState() {
    super.initState();
    _apiService = ApiService.instance;

    _animationController = AnimationController(
      duration: const Duration(milliseconds: 500),
      vsync: this,
    );

    _slideController = AnimationController(
      duration: const Duration(milliseconds: 400),
      vsync: this,
    );

    // Инициализация ValueNotifiers
    _currentWeekStart = ValueNotifier<DateTime>(
      DateTime.now().subtract(
        Duration(days: DateTime.now().weekday),
      ),
    );
    _financeInfo = ValueNotifier<FinanceInfoResponse?>(null);
    _isLoading = ValueNotifier<bool>(false);

    fetchFinanceInfo();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _updateScreenMetrics();
  }

  void _updateScreenMetrics() {
    _screenSize = MediaQuery.of(context).size;
    _isSmallScreen = _screenSize.width < _smallScreenThreshold;
  }

  @override
  void dispose() {
    _animationController.dispose();
    _slideController.dispose();
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
      _animationController.forward(from: 0.0);
      _slideController.forward(from: 0.0);
    } catch (e) {
      if (mounted) {
        final l10n = context.read<LocalizationProvider>().localizations;
        _showModernSnackBar(
          '${l10n.get('finance.errorLoadingData')}: $e',
          isError: true,
        );
      }
    } finally {
      if (mounted) {
        _isLoading.value = false;
      }
    }
  }

  void _showModernSnackBar(String message, {bool isError = false}) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            Icon(
              isError ? Icons.error_outline : Icons.check_circle_outline,
              color: Colors.white,
            ),
            const SizedBox(width: 12),
            Expanded(child: Text(message)),
          ],
        ),
        backgroundColor: isError ? _errorColor : _successColor,
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        margin: const EdgeInsets.all(20),
        elevation: 8,
      ),
    );
  }

  void changeWeek(int days) {
    HapticFeedback.lightImpact();
    _currentWeekStart.value = _currentWeekStart.value.add(Duration(days: days));
    fetchFinanceInfo();
  }

  String formatMoney(double amount) {
    return '\$${amount.toStringAsFixed(2)}';
  }

  // TODO: Раскомментировать когда понадобятся налоги
  // double calculateTaxes() {
  //   final info = _financeInfo.value;
  //   if (info == null) return 0.0;
  //   return info.totalGrossPay - info.totalNetPay;
  // }

  Future<void> _downloadFinanceReport() async {
    final info = _financeInfo.value;
    if (info != null) {
      final l10n = context.read<LocalizationProvider>().localizations;
      HapticFeedback.mediumImpact();

      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (context) => Dialog(
          backgroundColor: Colors.transparent,
          child: Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(20),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.1),
                  blurRadius: 20,
                  offset: const Offset(0, 10),
                ),
              ],
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Container(
                  width: 60,
                  height: 60,
                  decoration: BoxDecoration(
                    color: _primaryColor,
                    borderRadius: BorderRadius.circular(15),
                  ),
                  child: const Center(
                    child: CircularProgressIndicator(
                      color: Colors.white,
                      strokeWidth: 3,
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                Text(
                  l10n.get('finance.downloadPdfReport'),
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ],
            ),
          ),
        ),
      );

      try {
        await FinancePdfService.generateFinanceReport(info);
        if (!mounted) return;
        Navigator.of(context).pop();
        _showModernSnackBar(
          l10n.get('finance.reportDownloadedSuccessfully'),
          isError: false,
        );
      } catch (e) {
        if (!mounted) return;
        Navigator.of(context).pop();
        _showModernSnackBar(
          '${l10n.get('finance.errorDownloadingReport')}: $e',
          isError: true,
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    _updateScreenMetrics();
    final l10n = context.read<LocalizationProvider>().localizations;

    return Scaffold(
      backgroundColor: _backgroundColor,
      body: ValueListenableBuilder<DateTime>(
        valueListenable: _currentWeekStart,
        builder: (context, currentWeekStart, _) {
          String periodText = '${DateFormat('MM/dd/yy').format(currentWeekStart)} - '
              '${DateFormat('MM/dd/yy').format(currentWeekStart.add(const Duration(days: 6)))}';

          return CustomScrollView(
            physics: const BouncingScrollPhysics(),
            slivers: [
              // Minimalist App Bar
              SliverAppBar(
                expandedHeight: 140,
                floating: false,
                pinned: true,
                elevation: 0,
                backgroundColor: Colors.white,
                flexibleSpace: FlexibleSpaceBar(
                  background: Container(
                    color: Colors.white,
                    child: SafeArea(
                      child: Column(
                        children: [
                          // Top Actions Row
                          Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                IconButton(
                                  icon: const Icon(
                                    Icons.arrow_back_ios_new,
                                    color: _primaryColor,
                                    size: 20,
                                  ),
                                  onPressed: () {
                                    HapticFeedback.lightImpact();
                                    Navigator.of(context).pop();
                                  },
                                ),
                                Row(
                                  children: [
                                    _buildActionButton(
                                      Icons.download_rounded,
                                      _downloadFinanceReport,
                                      l10n.get('finance.downloadTooltip'),
                                    ),
                                    const SizedBox(width: 8),
                                    _buildActionButton(
                                      Icons.share_rounded,
                                          () {
                                        HapticFeedback.lightImpact();
                                        final info = _financeInfo.value;
                                        if (info != null) {
                                          Share.share(
                                            '${l10n.get('finance.shareTitle')} ($periodText):\n'
                                                '${l10n.get('finance.totalHours')}: ${info.totalHoursWorked.toStringAsFixed(1)}\n'
                                                '${l10n.get('finance.totalGrossPay')}: ${formatMoney(info.totalGrossPay)}',
                                            // TODO: Раскомментировать когда понадобятся налоги
                                            // '${l10n.get('finance.totalNetPay')}: ${formatMoney(info.totalNetPay)}\n'
                                            // '${l10n.get('finance.totalTaxes')}: ${formatMoney(calculateTaxes())}',
                                          );
                                        }
                                      },
                                      l10n.get('finance.shareTooltip'),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                          // Centered Title and Date
                          Expanded(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Text(
                                  l10n.get('finance.title'),
                                  style: const TextStyle(
                                    color: _primaryColor,
                                    fontSize: 32,
                                    fontWeight: FontWeight.bold,
                                    letterSpacing: -0.5,
                                  ),
                                ),
                                const SizedBox(height: 8),
                                Text(
                                  periodText,
                                  style: TextStyle(
                                    color: Colors.grey[600],
                                    fontSize: 15,
                                    fontWeight: FontWeight.w500,
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

              // Week Navigation
              SliverToBoxAdapter(
                child: Container(
                  margin: const EdgeInsets.all(16),
                  child: _buildWeekNavigation(l10n, _isSmallScreen, currentWeekStart),
                ),
              ),

              // Summary Cards - ТОЛЬКО HOURS И GROSS
              ValueListenableBuilder<bool>(
                valueListenable: _isLoading,
                builder: (context, isLoading, _) {
                  return ValueListenableBuilder<FinanceInfoResponse?>(
                    valueListenable: _financeInfo,
                    builder: (context, financeInfo, _) {
                      if (!isLoading && financeInfo != null) {
                        return SliverToBoxAdapter(
                          child: Container(
                            height: 180,
                            padding: const EdgeInsets.symmetric(horizontal: 16),
                            child: ListView(
                              scrollDirection: Axis.horizontal,
                              physics: const BouncingScrollPhysics(),
                              children: [
                                _buildModernSummaryCard(
                                  l10n.get('finance.hours'),
                                  '${financeInfo.totalHoursWorked.toStringAsFixed(1)}',
                                  Icons.schedule_rounded,
                                  const [Color(0xFF4A5568), _primaryColor],
                                  0,
                                ),
                                _buildModernSummaryCard(
                                  l10n.get('finance.gross'),
                                  formatMoney(financeInfo.totalGrossPay),
                                  Icons.trending_up_rounded,
                                  const [Color(0xFF48BB78), Color(0xFF38A169)],
                                  1,
                                ),
                                // TODO: Раскомментировать когда понадобятся налоги
                                // _buildModernSummaryCard(
                                //   l10n.get('finance.net'),
                                //   formatMoney(financeInfo.totalNetPay),
                                //   Icons.account_balance_wallet_rounded,
                                //   const [Color(0xFF4299E1), Color(0xFF3182CE)],
                                //   2,
                                // ),
                                // _buildModernSummaryCard(
                                //   l10n.get('finance.totalTaxes'),
                                //   formatMoney(calculateTaxes()),
                                //   Icons.receipt_long_rounded,
                                //   const [Color(0xFFED8936), Color(0xFFDD6B20)],
                                //   3,
                                // ),
                              ],
                            ),
                          ),
                        );
                      }
                      return const SliverToBoxAdapter(child: SizedBox.shrink());
                    },
                  );
                },
              ),

              // Daily Breakdown Title
              const SliverToBoxAdapter(
                child: Padding(
                  padding: EdgeInsets.all(16),
                  child: Text(
                    'Daily Breakdown',
                    style: TextStyle(
                      color: Colors.black,
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      letterSpacing: -0.5,
                    ),
                  ),
                ),
              ),

              // Content with ValueListenableBuilders
              ValueListenableBuilder<bool>(
                valueListenable: _isLoading,
                builder: (context, isLoading, _) {
                  if (isLoading) {
                    return SliverFillRemaining(
                      child: Center(
                        child: Container(
                          width: 80,
                          height: 80,
                          decoration: BoxDecoration(
                            color: _primaryColor,
                            borderRadius: BorderRadius.circular(20),
                            boxShadow: [
                              BoxShadow(
                                color: _primaryColor.withOpacity(0.2),
                                blurRadius: 20,
                                offset: const Offset(0, 10),
                              ),
                            ],
                          ),
                          child: const Center(
                            child: CircularProgressIndicator(
                              color: Colors.white,
                              strokeWidth: 3,
                            ),
                          ),
                        ),
                      ),
                    );
                  }

                  return ValueListenableBuilder<FinanceInfoResponse?>(
                    valueListenable: _financeInfo,
                    builder: (context, financeInfo, _) {
                      if (financeInfo?.dailyInfo.isEmpty ?? true) {
                        return SliverFillRemaining(
                          child: Center(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Container(
                                  width: 100,
                                  height: 100,
                                  decoration: BoxDecoration(
                                    gradient: LinearGradient(
                                      colors: [
                                        Colors.grey.shade300,
                                        Colors.grey.shade400,
                                      ],
                                    ),
                                    shape: BoxShape.circle,
                                  ),
                                  child: const Icon(
                                    Icons.event_busy_rounded,
                                    size: 50,
                                    color: Colors.white,
                                  ),
                                ),
                                const SizedBox(height: 24),
                                Text(
                                  l10n.get('finance.noDataForPeriod'),
                                  style: TextStyle(
                                    fontSize: 18,
                                    color: Colors.grey.shade700,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                                const SizedBox(height: 16),
                                ElevatedButton(
                                  onPressed: () {
                                    HapticFeedback.lightImpact();
                                    fetchFinanceInfo();
                                  },
                                  style: ElevatedButton.styleFrom(
                                    backgroundColor: _primaryColor,
                                    foregroundColor: Colors.white,
                                    padding: const EdgeInsets.symmetric(
                                      horizontal: 32,
                                      vertical: 16,
                                    ),
                                    shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(12),
                                    ),
                                    elevation: 0,
                                  ),
                                  child: const Row(
                                    mainAxisSize: MainAxisSize.min,
                                    children: [
                                      Icon(Icons.refresh_rounded),
                                      SizedBox(width: 8),
                                      Text('Refresh'),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          ),
                        );
                      }

                      return SliverList(
                        delegate: SliverChildBuilderDelegate(
                              (context, index) {
                            if (index == financeInfo!.dailyInfo.length) {
                              return _buildTotalRow(l10n, financeInfo);
                            }

                            final daily = financeInfo.dailyInfo[index];
                            return _buildDailyCard(daily, index);
                          },
                          childCount: financeInfo!.dailyInfo.length + 1,
                        ),
                      );
                    },
                  );
                },
              ),

              // Bottom Padding
              const SliverToBoxAdapter(
                child: SizedBox(height: 100),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildActionButton(IconData icon, VoidCallback onPressed, String tooltip) {
    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onPressed,
        borderRadius: BorderRadius.circular(12),
        child: Tooltip(
          message: tooltip,
          child: Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: Colors.grey[100],
              borderRadius: BorderRadius.circular(12),
              border: Border.all(
                color: Colors.grey[300]!,
                width: 1,
              ),
            ),
            child: Icon(
              icon,
              color: _primaryColor,
              size: 22,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildWeekNavigation(dynamic l10n, bool isSmallScreen, DateTime currentWeekStart) {
    return Container(
      padding: const EdgeInsets.all(2),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(14),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.08),
            blurRadius: 8,
            offset: const Offset(0, 3),
          ),
        ],
      ),
      child: Row(
        children: [
          // Previous Week Button
          Expanded(
            flex: 2,
            child: Material(
              color: Colors.transparent,
              borderRadius: BorderRadius.circular(12),
              child: InkWell(
                onTap: () => changeWeek(-7),
                borderRadius: BorderRadius.circular(12),
                child: Container(
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  decoration: BoxDecoration(
                    color: _primaryColor.withOpacity(0.08),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(
                        Icons.chevron_left_rounded,
                        color: _primaryColor,
                        size: 22,
                      ),
                      Text(
                        l10n.get('finance.previous'),
                        style: const TextStyle(
                          color: _primaryColor,
                          fontWeight: FontWeight.w600,
                          fontSize: 14,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
          // Current Period Display
          Expanded(
            flex: 3,
            child: Container(
              padding: const EdgeInsets.symmetric(vertical: 8),
              child: Column(
                children: [
                  Text(
                    l10n.get('finance.weekPeriod').toUpperCase(),
                    style: TextStyle(
                      fontSize: 10,
                      color: Colors.grey.shade500,
                      fontWeight: FontWeight.w600,
                      letterSpacing: 0.8,
                    ),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    DateFormat('MMM d').format(currentWeekStart),
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: _primaryColor,
                    ),
                  ),
                  Text(
                    'to ${DateFormat('MMM d, yyyy').format(currentWeekStart.add(const Duration(days: 6)))}',
                    style: TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.w500,
                      color: Colors.grey.shade700,
                    ),
                  ),
                ],
              ),
            ),
          ),
          // Next Week Button
          Expanded(
            flex: 2,
            child: Material(
              color: Colors.transparent,
              borderRadius: BorderRadius.circular(12),
              child: InkWell(
                onTap: () => changeWeek(7),
                borderRadius: BorderRadius.circular(12),
                child: Container(
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  decoration: BoxDecoration(
                    color: _primaryColor.withOpacity(0.08),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        l10n.get('finance.next'),
                        style: const TextStyle(
                          color: _primaryColor,
                          fontWeight: FontWeight.w600,
                          fontSize: 14,
                        ),
                      ),
                      const Icon(
                        Icons.chevron_right_rounded,
                        color: _primaryColor,
                        size: 22,
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildModernSummaryCard(
      String label,
      String value,
      IconData icon,
      List<Color> gradientColors,
      int index,
      ) {
    return Container(
      width: 150,
      margin: const EdgeInsets.only(right: 12),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: gradientColors,
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: gradientColors.first.withOpacity(0.25),
            blurRadius: 12,
            offset: const Offset(0, 6),
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: () => HapticFeedback.lightImpact(),
          borderRadius: BorderRadius.circular(20),
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.2),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Icon(
                    icon,
                    color: Colors.white,
                    size: 24,
                  ),
                ),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      label,
                      style: TextStyle(
                        color: Colors.white.withOpacity(0.9),
                        fontSize: 12,
                        fontWeight: FontWeight.w500,
                        letterSpacing: 0.5,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      value,
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        letterSpacing: -0.5,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildDailyCard(DailyFinanceInfo daily, int index) {
    final bool hasData = daily.hoursWorked > 0;

    return FadeTransition(
      opacity: Tween<double>(
        begin: 0,
        end: 1,
      ).animate(
        CurvedAnimation(
          parent: _animationController,
          curve: Interval(
            index * 0.05,
            0.3 + index * 0.05,
            curve: Curves.easeOut,
          ),
        ),
      ),
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          boxShadow: hasData
              ? [
            BoxShadow(
              color: Colors.grey.withOpacity(0.06),
              blurRadius: 8,
              offset: const Offset(0, 3),
            ),
          ]
              : [],
          border: Border.all(
            color: hasData ? Colors.transparent : Colors.grey.shade200,
            width: 1,
          ),
        ),
        child: Material(
          color: Colors.transparent,
          borderRadius: BorderRadius.circular(16),
          child: InkWell(
            onTap: hasData ? () => HapticFeedback.lightImpact() : null,
            borderRadius: BorderRadius.circular(16),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  // Date Badge
                  Container(
                    width: 60,
                    height: 60,
                    decoration: BoxDecoration(
                      color: hasData
                          ? _primaryColor
                          : Colors.grey.shade200,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          DateFormat('EEE').format(daily.date).toUpperCase(),
                          style: TextStyle(
                            color: hasData ? Colors.white : Colors.grey.shade500,
                            fontSize: 11,
                            fontWeight: FontWeight.bold,
                            letterSpacing: 0.5,
                          ),
                        ),
                        const SizedBox(height: 2),
                        Text(
                          DateFormat('dd').format(daily.date),
                          style: TextStyle(
                            color: hasData ? Colors.white : Colors.grey.shade500,
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(width: 16),
                  // Data - ТОЛЬКО HOURS И GROSS
                  Expanded(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                      children: [
                        _buildDataItem(
                          Icons.schedule,
                          '${daily.hoursWorked.toStringAsFixed(1)}h',
                          hasData,
                        ),
                        _buildDataItem(
                          Icons.attach_money,
                          formatMoney(daily.grossPay),
                          hasData,
                        ),
                        // TODO: Раскомментировать когда понадобится net pay
                        // _buildDataItem(
                        //   Icons.account_balance_wallet,
                        //   formatMoney(daily.netPay),
                        //   hasData,
                        // ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildDataItem(IconData icon, String value, bool hasData) {
    return Column(
      children: [
        Icon(
          icon,
          size: 16,
          color: hasData ? _primaryColor : Colors.grey.shade400,
        ),
        const SizedBox(height: 4),
        Text(
          value,
          style: TextStyle(
            fontSize: 14,
            fontWeight: hasData ? FontWeight.w600 : FontWeight.normal,
            color: hasData ? Colors.grey.shade800 : Colors.grey.shade400,
          ),
        ),
      ],
    );
  }

  Widget _buildTotalRow(dynamic l10n, FinanceInfoResponse financeInfo) {
    return Container(
      margin: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [
            _primaryColor,
            Color(0xFF4A5568),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: _primaryColor.withOpacity(0.2),
            blurRadius: 20,
            offset: const Offset(0, 10),
          ),
        ],
      ),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.assessment_rounded,
                  color: Colors.white.withOpacity(0.9),
                  size: 20,
                ),
                const SizedBox(width: 8),
                Text(
                  l10n.get('finance.totals').toUpperCase(),
                  style: TextStyle(
                    color: Colors.white.withOpacity(0.9),
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                    letterSpacing: 1.5,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _buildTotalItem(
                  Icons.schedule_rounded,
                  '${financeInfo.totalHoursWorked.toStringAsFixed(1)}',
                  'hours',
                ),
                Container(
                  width: 1,
                  height: 40,
                  color: Colors.white.withOpacity(0.2),
                ),
                _buildTotalItem(
                  Icons.trending_up_rounded,
                  formatMoney(financeInfo.totalGrossPay),
                  'gross',
                ),
                // TODO: Раскомментировать когда понадобится net
                // Container(
                //   width: 1,
                //   height: 40,
                //   color: Colors.white.withOpacity(0.2),
                // ),
                // _buildTotalItem(
                //   Icons.account_balance_wallet_rounded,
                //   formatMoney(financeInfo.totalNetPay),
                //   'net',
                // ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTotalItem(IconData icon, String value, String label) {
    return Column(
      children: [
        Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.15),
            shape: BoxShape.circle,
          ),
          child: Icon(
            icon,
            color: Colors.white,
            size: 18,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          value,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 18,
            fontWeight: FontWeight.bold,
            letterSpacing: -0.5,
          ),
        ),
        Text(
          label,
          style: TextStyle(
            color: Colors.white.withOpacity(0.8),
            fontSize: 11,
            letterSpacing: 0.5,
          ),
        ),
      ],
    );
  }
}