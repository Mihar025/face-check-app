import 'package:face_check/localization/app_localizations.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';
import '../../../models/daily_earnings.dart';
import '../../../providers/localization_provider.dart';
import '../../../services/ApiService.dart';
import 'components/info_row.dart';
import 'components/progress_circle/progress_circle.dart';
import 'utils/date_formatter.dart';

class ViewDetailsScreen extends StatefulWidget {
  final double workedHours;

  const ViewDetailsScreen({
    super.key,
    required this.workedHours,
  });

  @override
  State<ViewDetailsScreen> createState() => _ViewDetailsScreenState();
}

class _ViewDetailsScreenState extends State<ViewDetailsScreen> with TickerProviderStateMixin {
  // Animation controllers
  late AnimationController _fadeController;
  late AnimationController _slideController;

  // ValueNotifiers для оптимизации перерисовок
  late final ValueNotifier<bool> _isLoading;
  late final ValueNotifier<double> _baseHourRate;
  late final ValueNotifier<double> _weekGrossAmount;
  late final ValueNotifier<List<DailyEarning>> _weeklyEarnings;
  late final ValueNotifier<bool> _hasCurrentPeriodData;

  // Кэшированные значения
  late Size _screenSize;
  late bool _isSmallScreen;
  late double _padding;
  late double _sectionSpacing;
  late AppLocalizations _l10n;
  late ThemeData _theme;
  late bool _isDark;

  // Кэшированные вычисления
  double? _cachedOvertimeHours;
  double? _cachedMissedHours;
  double? _cachedMaxEarning;
  String? _cachedPeriod;

  // Предопределенные константы
  static const double _largeSpacing = 40.0;
  static const double _smallSpacing = 30.0;
  static const double _smallScreenThreshold = 400.0;

  @override
  void initState() {
    super.initState();

    // Инициализация анимаций
    _fadeController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    _slideController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );

    // Инициализация ValueNotifiers
    _isLoading = ValueNotifier<bool>(true);
    _baseHourRate = ValueNotifier<double>(0.0);
    _weekGrossAmount = ValueNotifier<double>(0.0);
    _weeklyEarnings = ValueNotifier<List<DailyEarning>>([]);
    _hasCurrentPeriodData = ValueNotifier<bool>(false);

    // Кэшируем период один раз
    _cachedPeriod = DateFormatter.getCurrentPeriod();

    _loadData();
    _fadeController.forward();
    _slideController.forward();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _updateCachedValues();
  }

  void _updateCachedValues() {
    _screenSize = MediaQuery.of(context).size;
    _isSmallScreen = _screenSize.width < _smallScreenThreshold;
    _padding = _isSmallScreen ? 12.0 : 16.0;
    _sectionSpacing = _isSmallScreen ? _smallSpacing : _largeSpacing;
    _l10n = context.read<LocalizationProvider>().localizations;
    _theme = Theme.of(context);
    _isDark = _theme.brightness == Brightness.dark;
  }

  @override
  void dispose() {
    _fadeController.dispose();
    _slideController.dispose();
    _isLoading.dispose();
    _baseHourRate.dispose();
    _weekGrossAmount.dispose();
    _weeklyEarnings.dispose();
    _hasCurrentPeriodData.dispose();
    super.dispose();
  }

  bool _isCurrentPeriodData(List<DailyEarning> earnings) {
    if (earnings.isEmpty) return false;

    final now = DateTime.now();
    // Получаем начало текущей недели (воскресенье)
    final startOfWeek = now.subtract(Duration(days: now.weekday % 7));
    final endOfWeek = startOfWeek.add(const Duration(days: 6));

    // Проверяем, есть ли хотя бы одна запись за текущую неделю
    return earnings.any((earning) {
      final date = earning.date;
      return date.isAfter(startOfWeek.subtract(const Duration(days: 1))) &&
          date.isBefore(endOfWeek.add(const Duration(days: 1)));
    });
  }

  Future<void> _loadData() async {
    try {
      final futures = await Future.wait([
        ApiService.instance.userApi.findWorkerBaseHourRate(),
        ApiService.instance.userApi.findWorkerSalaryPerWeekGross(),
        ApiService.instance.getWeeklyEarnings(),
      ]);

      if (!mounted) return;

      _baseHourRate.value = (futures[0] as dynamic).data?.toDouble() ?? 0.0;

      final earnings = futures[2] as List<DailyEarning>;
      _weeklyEarnings.value = earnings;

      // Проверяем актуальность данных
      final isCurrentPeriod = _isCurrentPeriodData(earnings);
      _hasCurrentPeriodData.value = isCurrentPeriod;

      // Если данные актуальны, используем их, иначе обнуляем
      if (isCurrentPeriod) {
        _weekGrossAmount.value = (futures[1] as dynamic).data?.toDouble() ?? 0.0;
      } else {
        _weekGrossAmount.value = 0.0;
      }

      // Кэшируем максимальное значение после загрузки
      _updateCachedMaxEarning();

      _isLoading.value = false;
    } catch (e) {
      if (mounted) {
        _isLoading.value = false;
      }
    }
  }

  double get overtimeHours {
    if (!_hasCurrentPeriodData.value) return 0.0;
    _cachedOvertimeHours ??= widget.workedHours > 40 ? widget.workedHours - 40 : 0.0;
    return _cachedOvertimeHours!;
  }

  double get missedHours {
    if (!_hasCurrentPeriodData.value) return 40.0;
    _cachedMissedHours ??= widget.workedHours < 40 ? 40 - widget.workedHours : 0.0;
    return _cachedMissedHours!;
  }

  void _updateCachedMaxEarning() {
    final earnings = _weeklyEarnings.value;
    _cachedMaxEarning = earnings.isEmpty
        ? 100.0
        : earnings.map((e) => e.netPay).reduce((a, b) => a > b ? a : b);
  }

  double get maxEarning => _cachedMaxEarning ?? 100.0;

  String _formatCurrency(double value) => '\$${value.toStringAsFixed(2)}';

  @override
  Widget build(BuildContext context) {
    _updateCachedValues();

    // Цвета в зависимости от темы
    final backgroundColor = _isDark ? Colors.grey[900]! : Colors.grey[50]!;
    final cardColor = _isDark ? Colors.grey[850]! : Colors.white;
    final textColor = _isDark ? Colors.grey[100]! : Colors.grey[900]!;
    final subtitleColor = _isDark ? Colors.grey[400]! : Colors.grey[600]!;

    return Scaffold(
      backgroundColor: backgroundColor,
      appBar: AppBar(
        elevation: 0,
        backgroundColor: Colors.transparent,
        title: FadeTransition(
          opacity: _fadeController,
          child: Text(
            _l10n.get('productivity'),
            style: GoogleFonts.poppins(
              fontWeight: FontWeight.w700,
              fontSize: 24,
              color: textColor,
            ),
          ),
        ),
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios_new, color: textColor),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: ValueListenableBuilder<bool>(
        valueListenable: _isLoading,
        builder: (context, isLoading, child) {
          if (isLoading) {
            return Center(
              child: Container(
                width: 60,
                height: 60,
                decoration: BoxDecoration(
                  color: Colors.blue.shade600,
                  borderRadius: BorderRadius.circular(15),
                ),
                child: const Center(
                  child: CircularProgressIndicator(
                    color: Colors.white,
                    strokeWidth: 3,
                  ),
                ),
              ),
            );
          }

          return ValueListenableBuilder<bool>(
            valueListenable: _hasCurrentPeriodData,
            builder: (context, hasData, _) {
              // Если нет данных за текущий период, показываем сообщение
              if (!hasData) {
                return _buildNoDataView(textColor, subtitleColor);
              }

              return SingleChildScrollView(
                physics: const BouncingScrollPhysics(),
                child: Padding(
                  padding: EdgeInsets.all(_padding),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // HERO SECTION - Combined Stats
                      SlideTransition(
                        position: Tween<Offset>(
                          begin: const Offset(0, -0.2),
                          end: Offset.zero,
                        ).animate(CurvedAnimation(
                          parent: _slideController,
                          curve: Curves.easeOutCubic,
                        )),
                        child: _buildHeroSection(),
                      ),

                      SizedBox(height: _sectionSpacing),

                      // PRODUCTIVITY METRICS
                      FadeTransition(
                        opacity: _fadeController,
                        child: _buildProductivityMetrics(textColor, subtitleColor, cardColor),
                      ),

                      SizedBox(height: _sectionSpacing),

                      // EARNINGS OVERVIEW
                      ValueListenableBuilder<double>(
                        valueListenable: _baseHourRate,
                        builder: (context, baseRate, _) {
                          return ValueListenableBuilder<double>(
                            valueListenable: _weekGrossAmount,
                            builder: (context, grossAmount, _) {
                              return FadeTransition(
                                opacity: _fadeController,
                                child: _buildEarningsOverview(
                                  baseRate,
                                  grossAmount,
                                  cardColor,
                                  textColor,
                                  subtitleColor,
                                ),
                              );
                            },
                          );
                        },
                      ),

                      SizedBox(height: _sectionSpacing),

                      // WEEKLY PERFORMANCE CHART
                      ValueListenableBuilder<List<DailyEarning>>(
                        valueListenable: _weeklyEarnings,
                        builder: (context, earnings, _) {
                          return SlideTransition(
                            position: Tween<Offset>(
                              begin: const Offset(0, 0.2),
                              end: Offset.zero,
                            ).animate(CurvedAnimation(
                              parent: _slideController,
                              curve: Curves.easeOutCubic,
                            )),
                            child: _buildWeeklyPerformance(
                              earnings,
                              cardColor,
                              textColor,
                              subtitleColor,
                            ),
                          );
                        },
                      ),

                      const SizedBox(height: 30),
                    ],
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }

  Widget _buildNoDataView(Color textColor, Color subtitleColor) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.calendar_today_outlined,
              size: 80,
              color: subtitleColor.withOpacity(0.3),
            ),
            const SizedBox(height: 24),
            Text(
              'No Data for Current Period',
              style: GoogleFonts.poppins(
                fontSize: 20,
                fontWeight: FontWeight.w600,
                color: textColor,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              'Period: $_cachedPeriod',
              style: GoogleFonts.poppins(
                fontSize: 14,
                color: subtitleColor,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'No hours recorded for this week',
              style: GoogleFonts.poppins(
                fontSize: 14,
                color: subtitleColor,
              ),
            ),
            const SizedBox(height: 32),
            ElevatedButton.icon(
              onPressed: () => Navigator.pop(context),
              icon: const Icon(Icons.arrow_back),
              label: const Text('Go Back'),
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.blue.shade600,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  // HERO SECTION с ключевыми метриками
  Widget _buildHeroSection() {
    final displayHours = _hasCurrentPeriodData.value ? widget.workedHours : 0.0;

    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [Colors.blue.shade600, Colors.blue.shade800],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(24),
        boxShadow: [
          BoxShadow(
            color: Colors.blue.shade600.withOpacity(0.3),
            blurRadius: 20,
            offset: const Offset(0, 10),
          ),
        ],
      ),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.calendar_today, color: Colors.white.withOpacity(0.9), size: 20),
              const SizedBox(width: 8),
              Text(
                _cachedPeriod!,
                style: GoogleFonts.poppins(
                  color: Colors.white.withOpacity(0.9),
                  fontSize: 14,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ),
          const SizedBox(height: 20),
          Row(
            children: [
              // Worked Hours Circle
              Expanded(
                child: Column(
                  children: [
                    Container(
                      width: 120,
                      height: 120,
                      decoration: BoxDecoration(
                        color: Colors.white,
                        shape: BoxShape.circle,
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withOpacity(0.1),
                            blurRadius: 10,
                          ),
                        ],
                      ),
                      child: Center(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(
                              displayHours.toStringAsFixed(1),
                              style: GoogleFonts.poppins(
                                fontSize: 32,
                                fontWeight: FontWeight.bold,
                                color: Colors.blue.shade700,
                              ),
                            ),
                            Text(
                              'Hours',
                              style: GoogleFonts.poppins(
                                fontSize: 14,
                                color: Colors.grey[600]!,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 20),
              // Stats Column
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildStatRow(
                      icon: Icons.trending_up,
                      label: 'Overtime',
                      value: '${overtimeHours.toStringAsFixed(1)}h',
                      color: Colors.green,
                    ),
                    const SizedBox(height: 12),
                    _buildStatRow(
                      icon: Icons.trending_down,
                      label: 'Missed',
                      value: '${missedHours.toStringAsFixed(1)}h',
                      color: Colors.orange,
                    ),
                    const SizedBox(height: 12),
                    _buildStatRow(
                      icon: Icons.check_circle,
                      label: 'Status',
                      value: displayHours >= 40 ? 'Complete' : 'In Progress',
                      color: displayHours >= 40 ? Colors.green : Colors.blue,
                    ),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStatRow({
    required IconData icon,
    required String label,
    required String value,
    required Color color,
  }) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(6),
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.2),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Icon(icon, color: Colors.white, size: 16),
        ),
        const SizedBox(width: 8),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                label,
                style: GoogleFonts.poppins(
                  color: Colors.white.withOpacity(0.8),
                  fontSize: 11,
                ),
              ),
              Text(
                value,
                style: GoogleFonts.poppins(
                  color: Colors.white,
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  // PRODUCTIVITY METRICS
  Widget _buildProductivityMetrics(Color textColor, Color subtitleColor, Color cardColor) {
    final displayHours = _hasCurrentPeriodData.value ? widget.workedHours : 0.0;
    final productivity = (displayHours / 40 * 100).clamp(0, 150);
    final efficiency = displayHours > 0
        ? ((40 - missedHours) / 40 * 100).clamp(0, 100)
        : 0.0;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Productivity Metrics',
          style: GoogleFonts.poppins(
            fontSize: 18,
            fontWeight: FontWeight.w600,
            color: textColor,
          ),
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            Expanded(
              child: _buildMetricCard(
                title: 'Productivity',
                value: '${productivity.toStringAsFixed(0)}%',
                icon: Icons.speed,
                color: Colors.purple,
                progress: productivity / 100,
                cardColor: cardColor,
                subtitleColor: subtitleColor,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _buildMetricCard(
                title: 'Efficiency',
                value: '${efficiency.toStringAsFixed(0)}%',
                icon: Icons.insights,
                color: Colors.indigo,
                progress: efficiency / 100,
                cardColor: cardColor,
                subtitleColor: subtitleColor,
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildMetricCard({
    required String title,
    required String value,
    required IconData icon,
    required Color color,
    required double progress,
    required Color cardColor,
    required Color subtitleColor,
  }) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: cardColor,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: _isDark
                ? Colors.black.withOpacity(0.2)
                : Colors.grey.withOpacity(0.1),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: color.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Icon(icon, color: color, size: 20),
              ),
              const Spacer(),
              Text(
                value,
                style: GoogleFonts.poppins(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: color,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            title,
            style: GoogleFonts.poppins(
              fontSize: 12,
              color: subtitleColor,
            ),
          ),
          const SizedBox(height: 8),
          LinearProgressIndicator(
            value: progress,
            backgroundColor: _isDark
                ? Colors.grey[800]!
                : Colors.grey[200]!,
            valueColor: AlwaysStoppedAnimation<Color>(color),
            minHeight: 6,
          ),
        ],
      ),
    );
  }

  // EARNINGS OVERVIEW
  Widget _buildEarningsOverview(
      double baseRate,
      double grossAmount,
      Color cardColor,
      Color textColor,
      Color subtitleColor,
      ) {
    final projectedEarnings = baseRate * 40;
    final actualVsProjected = grossAmount > 0
        ? ((grossAmount / projectedEarnings) * 100).clamp(0, 200)
        : 0.0;

    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: cardColor,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: _isDark
                ? Colors.black.withOpacity(0.2)
                : Colors.grey.withOpacity(0.1),
            blurRadius: 15,
            offset: const Offset(0, 5),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: Colors.green.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(Icons.attach_money, color: Colors.green.shade700, size: 24),
              ),
              const SizedBox(width: 12),
              Text(
                'Earnings Overview',
                style: GoogleFonts.poppins(
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                  color: textColor,
                ),
              ),
            ],
          ),
          const SizedBox(height: 20),

          // Key Metrics
          Row(
            children: [
              Expanded(
                child: _buildEarningMetric(
                  label: 'Hourly Rate',
                  value: _formatCurrency(baseRate),
                  icon: Icons.schedule,
                  color: Colors.blue,
                  textColor: textColor,
                  subtitleColor: subtitleColor,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: _buildEarningMetric(
                  label: 'Week Total',
                  value: _formatCurrency(grossAmount),
                  icon: Icons.account_balance_wallet,
                  color: Colors.green,
                  textColor: textColor,
                  subtitleColor: subtitleColor,
                ),
              ),
            ],
          ),

          const SizedBox(height: 20),

          // Progress Bar
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    'Earning Progress',
                    style: GoogleFonts.poppins(
                      fontSize: 13,
                      color: subtitleColor,
                    ),
                  ),
                  Text(
                    '${actualVsProjected.toStringAsFixed(0)}% of target',
                    style: GoogleFonts.poppins(
                      fontSize: 13,
                      fontWeight: FontWeight.w600,
                      color: Colors.green.shade700,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              ClipRRect(
                borderRadius: BorderRadius.circular(10),
                child: LinearProgressIndicator(
                  value: (actualVsProjected / 100).clamp(0, 1),
                  backgroundColor: _isDark
                      ? Colors.grey[800]!
                      : Colors.grey[200]!,
                  valueColor: AlwaysStoppedAnimation<Color>(
                    actualVsProjected >= 100 ? Colors.green : Colors.blue,
                  ),
                  minHeight: 10,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildEarningMetric({
    required String label,
    required String value,
    required IconData icon,
    required Color color,
    required Color textColor,
    required Color subtitleColor,
  }) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: color.withOpacity(0.05),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: color.withOpacity(0.2),
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, color: color, size: 16),
              const SizedBox(width: 6),
              Text(
                label,
                style: GoogleFonts.poppins(
                  fontSize: 11,
                  color: subtitleColor,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            value,
            style: GoogleFonts.poppins(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: textColor,
            ),
          ),
        ],
      ),
    );
  }

  // WEEKLY PERFORMANCE
  Widget _buildWeeklyPerformance(
      List<DailyEarning> earnings,
      Color cardColor,
      Color textColor,
      Color subtitleColor,
      ) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: cardColor,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: _isDark
                ? Colors.black.withOpacity(0.2)
                : Colors.grey.withOpacity(0.1),
            blurRadius: 15,
            offset: const Offset(0, 5),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: Colors.purple.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(Icons.bar_chart, color: Colors.purple.shade700, size: 24),
              ),
              const SizedBox(width: 12),
              Text(
                'Weekly Performance',
                style: GoogleFonts.poppins(
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                  color: textColor,
                ),
              ),
            ],
          ),
          const SizedBox(height: 20),

          if (earnings.isEmpty)
            Center(
              child: Padding(
                padding: const EdgeInsets.all(40),
                child: Column(
                  children: [
                    Icon(Icons.info_outline, size: 48, color: subtitleColor.withOpacity(0.5)),
                    const SizedBox(height: 12),
                    Text(
                      'No data available',
                      style: GoogleFonts.poppins(
                        color: subtitleColor,
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ),
            )
          else
            SizedBox(
              height: 200,
              child: _buildBarChart(earnings, textColor, subtitleColor),
            ),
        ],
      ),
    );
  }

  Widget _buildBarChart(List<DailyEarning> earnings, Color textColor, Color subtitleColor) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final width = constraints.maxWidth;
        final barWidth = (width / earnings.length) * 0.6;
        final spacing = (width / earnings.length) * 0.4;

        return Row(
          crossAxisAlignment: CrossAxisAlignment.end,
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: earnings.map((earning) {
            final heightPercentage = maxEarning > 0
                ? (earning.netPay / maxEarning).clamp(0.0, 1.0)
                : 0.0;

            return Column(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Text(
                  '\$${earning.netPay.toStringAsFixed(0)}',
                  style: GoogleFonts.poppins(
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                    color: textColor,
                  ),
                ),
                const SizedBox(height: 4),
                AnimatedContainer(
                  duration: Duration(milliseconds: 800 + earnings.indexOf(earning) * 100),
                  width: barWidth.clamp(20, 50),
                  height: heightPercentage * 120,
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      colors: [
                        Colors.blue.shade400,
                        Colors.blue.shade600,
                      ],
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                    ),
                    borderRadius: const BorderRadius.only(
                      topLeft: Radius.circular(6),
                      topRight: Radius.circular(6),
                    ),
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  _formatDayLabel(earning.date),
                  style: GoogleFonts.poppins(
                    fontSize: 10,
                    color: subtitleColor,
                  ),
                ),
              ],
            );
          }).toList(),
        );
      },
    );
  }

  String _formatDayLabel(DateTime date) {
    final days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    return days[date.weekday - 1];
  }
}