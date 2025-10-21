import 'package:face_check/localization/app_localizations.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import 'dart:async';
import '../../../models/daily_earnings.dart';
import '../../../providers/localization_provider.dart';
import '../../../services/ApiService.dart';
import 'components/info_row.dart';
import 'components/progress_circle/progress_circle.dart';
import 'utils/date_formatter.dart';

// Менеджер кеша для ViewDetails
class ViewDetailsCacheManager {
  static const String _baseRateKey = 'view_details_base_rate';
  static const String _grossAmountKey = 'view_details_gross_amount';
  static const String _weeklyEarningsKey = 'view_details_weekly_earnings';
  static const String _cacheTimestampKey = 'view_details_cache_timestamp';
  static const int _cacheValidityMinutes = 30;

  static Future<Map<String, dynamic>?> getCachedData() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final timestamp = prefs.getInt(_cacheTimestampKey) ?? 0;
      final now = DateTime.now().millisecondsSinceEpoch;

      if (now - timestamp > _cacheValidityMinutes * 60 * 1000) {
        await clearCache();
        return null;
      }

      final earningsJson = prefs.getString(_weeklyEarningsKey);
      List<DailyEarning> earnings = [];

      if (earningsJson != null) {
        final List<dynamic> decodedList = json.decode(earningsJson);
        earnings = decodedList.map((e) => DailyEarning.fromJson(e)).toList();
      }

      return {
        'baseRate': prefs.getDouble(_baseRateKey) ?? 0.0,
        'grossAmount': prefs.getDouble(_grossAmountKey) ?? 0.0,
        'weeklyEarnings': earnings,
        'timestamp': timestamp,
      };
    } catch (e) {
      print('Error loading cached data: $e');
      return null;
    }
  }

  static Future<void> saveCachedData({
    required double baseRate,
    required double grossAmount,
    required List<DailyEarning> weeklyEarnings,
  }) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final earningsJson = json.encode(
          weeklyEarnings.map((e) => e.toJson()).toList()
      );

      await Future.wait([
        prefs.setDouble(_baseRateKey, baseRate),
        prefs.setDouble(_grossAmountKey, grossAmount),
        prefs.setString(_weeklyEarningsKey, earningsJson),
        prefs.setInt(_cacheTimestampKey, DateTime.now().millisecondsSinceEpoch),
      ]);
    } catch (e) {
      print('Error saving cached data: $e');
    }
  }

  static Future<void> clearCache() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await Future.wait([
        prefs.remove(_baseRateKey),
        prefs.remove(_grossAmountKey),
        prefs.remove(_weeklyEarningsKey),
        prefs.remove(_cacheTimestampKey),
      ]);
    } catch (e) {
      print('Error clearing cache: $e');
    }
  }

  static Future<bool> isCacheValid() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final timestamp = prefs.getInt(_cacheTimestampKey) ?? 0;
      final now = DateTime.now().millisecondsSinceEpoch;
      return (now - timestamp) <= (_cacheValidityMinutes * 60 * 1000);
    } catch (e) {
      return false;
    }
  }
}

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
  late AnimationController _fadeController;
  late AnimationController _slideController;

  late final ValueNotifier<bool> _isLoading;
  late final ValueNotifier<double> _baseHourRate;
  late final ValueNotifier<double> _weekGrossAmount;
  late final ValueNotifier<List<DailyEarning>> _weeklyEarnings;
  late final ValueNotifier<bool> _hasCurrentPeriodData;

  late Size _screenSize;
  late bool _isSmallScreen;
  late double _padding;
  late double _sectionSpacing;
  late AppLocalizations _l10n;
  late ThemeData _theme;
  late bool _isDark;

  double? _cachedOvertimeHours;
  double? _cachedMissedHours;
  double? _cachedMaxEarning;
  String? _cachedPeriod;

  bool _isDataFromCache = false;

  static const double _largeSpacing = 40.0;
  static const double _smallSpacing = 30.0;
  static const double _smallScreenThreshold = 400.0;

  @override
  void initState() {
    super.initState();

    _fadeController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
    _slideController = AnimationController(
      duration: const Duration(milliseconds: 600),
      vsync: this,
    );

    _isLoading = ValueNotifier<bool>(true);
    _baseHourRate = ValueNotifier<double>(0.0);
    _weekGrossAmount = ValueNotifier<double>(0.0);
    _weeklyEarnings = ValueNotifier<List<DailyEarning>>([]);
    _hasCurrentPeriodData = ValueNotifier<bool>(false);

    _cachedPeriod = DateFormatter.getCurrentPeriod();

    _initializeData();

    _fadeController.forward();
    _slideController.forward();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _updateCachedValues();
  }

  void _updateCachedValues() {
    _screenSize = MediaQuery
        .of(context)
        .size;
    _isSmallScreen = _screenSize.width < _smallScreenThreshold;
    _padding = _isSmallScreen ? 12.0 : 16.0;
    _sectionSpacing = _isSmallScreen ? _smallSpacing : _largeSpacing;
    _l10n = context
        .read<LocalizationProvider>()
        .localizations;
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
    final startOfWeek = now.subtract(Duration(days: now.weekday % 7));
    final endOfWeek = startOfWeek.add(const Duration(days: 6));

    return earnings.any((earning) {
      final date = earning.date;
      return date.isAfter(startOfWeek.subtract(const Duration(days: 1))) &&
          date.isBefore(endOfWeek.add(const Duration(days: 1)));
    });
  }

  Future<void> _initializeData() async {
    final stopwatch = Stopwatch()
      ..start();

    final cachedData = await ViewDetailsCacheManager.getCachedData();

    if (cachedData != null && mounted) {
      final earnings = cachedData['weeklyEarnings'] as List<DailyEarning>;
      final baseRate = cachedData['baseRate'] as double;

      if (earnings.isNotEmpty || baseRate > 0) {
        _baseHourRate.value = baseRate;
        _weekGrossAmount.value = cachedData['grossAmount'];
        _weeklyEarnings.value = earnings;

        final isCurrentPeriod = _isCurrentPeriodData(earnings);
        _hasCurrentPeriodData.value = isCurrentPeriod;

        if (!isCurrentPeriod) {
          _weekGrossAmount.value = 0.0;
        }

        _updateCachedMaxEarning();
        _isLoading.value = false;
        _isDataFromCache = true;

        print('Cache loaded in ${stopwatch.elapsedMilliseconds}ms');

        final cacheAge = DateTime
            .now()
            .millisecondsSinceEpoch - cachedData['timestamp'];
        if (cacheAge > 5 * 60 * 1000) {
          _loadDataInBackground();
        }
      } else {
        await _loadData();
      }
    } else {
      await _loadData();
    }

    stopwatch.stop();
  }

  Future<void> _loadDataInBackground() async {
    try {
      final stopwatch = Stopwatch()..start();

      // Создаём безопасные обёртки для каждого запроса
      Future<dynamic> safeBaseRate() async {
        try {
          return await ApiService.instance.userApi.findWorkerBaseHourRate();
        } catch (e) {
          print('Background: baseRate error: $e');
          return null;
        }
      }

      Future<dynamic> safeGrossAmount() async {
        try {
          return await ApiService.instance.userApi.findWorkerSalaryPerWeekGross();
        } catch (e) {
          print('Background: grossAmount error: $e');
          return null;
        }
      }

      Future<List<DailyEarning>> safeWeeklyEarnings() async {
        try {
          return await ApiService.instance.getWeeklyEarnings();
        } catch (e) {
          print('Background: weeklyEarnings error: $e');
          return <DailyEarning>[];
        }
      }

      final results = await Future.wait([
        safeBaseRate(),
        safeGrossAmount(),
        safeWeeklyEarnings(),
      ]).timeout(
        const Duration(seconds: 8),
        onTimeout: () => [null, null, <DailyEarning>[]],
      );

      if (!mounted) return;

      final baseRateResponse = results[0];
      final grossAmountResponse = results[1];
      final earningsResponse = results[2];

      final newBaseRate = baseRateResponse != null
          ? (baseRateResponse as dynamic).data?.toDouble() ?? _baseHourRate.value
          : _baseHourRate.value;

      final earnings = earningsResponse is List<DailyEarning>
          ? earningsResponse
          : <DailyEarning>[];

      final isCurrentPeriod = earnings.isNotEmpty ? _isCurrentPeriodData(earnings) : false;

      double newGrossAmount = 0.0;
      if (isCurrentPeriod && grossAmountResponse != null) {
        newGrossAmount = (grossAmountResponse as dynamic).data?.toDouble() ?? 0.0;
      }

      bool hasChanges = false;

      if (_baseHourRate.value != newBaseRate) {
        _baseHourRate.value = newBaseRate;
        hasChanges = true;
      }

      if (_weekGrossAmount.value != newGrossAmount) {
        _weekGrossAmount.value = newGrossAmount;
        hasChanges = true;
      }

      if (_weeklyEarnings.value.length != earnings.length ||
          (earnings.isNotEmpty && _weeklyEarnings.value.isNotEmpty &&
              earnings.first.netPay != _weeklyEarnings.value.first.netPay)) {
        _weeklyEarnings.value = earnings;
        _hasCurrentPeriodData.value = isCurrentPeriod;
        _updateCachedMaxEarning();
        hasChanges = true;
      }

      if (hasChanges && (newBaseRate > 0 || earnings.isNotEmpty)) {
        await ViewDetailsCacheManager.saveCachedData(
          baseRate: newBaseRate,
          grossAmount: newGrossAmount,
          weeklyEarnings: earnings,
        );

        if (_isDataFromCache && mounted) {
          setState(() => _isDataFromCache = false);
        }

        print('Background update completed in ${stopwatch.elapsedMilliseconds}ms');
      }

      stopwatch.stop();
    } catch (e) {
      print('Error loading data in background: $e');
    }
  }

  Future<void> _loadData({bool forceRefresh = false}) async {
    final stopwatch = Stopwatch()..start();

    if (forceRefresh) {
      await ViewDetailsCacheManager.clearCache();
    }

    try {
      // Безопасные обёртки
      Future<dynamic> safeBaseRate() async {
        try {
          return await ApiService.instance.userApi.findWorkerBaseHourRate();
        } catch (e) {
          print('LoadData: baseRate error: $e');
          return null;
        }
      }

      Future<dynamic> safeGrossAmount() async {
        try {
          return await ApiService.instance.userApi.findWorkerSalaryPerWeekGross();
        } catch (e) {
          print('LoadData: grossAmount error (500 expected): $e');
          return null;
        }
      }

      Future<List<DailyEarning>> safeWeeklyEarnings() async {
        try {
          return await ApiService.instance.getWeeklyEarnings();
        } catch (e) {
          print('LoadData: weeklyEarnings error: $e');
          return <DailyEarning>[];
        }
      }

      final results = await Future.wait([
        safeBaseRate(),
        safeGrossAmount(),
        safeWeeklyEarnings(),
      ]).timeout(
        const Duration(seconds: 10),
        onTimeout: () => [null, null, <DailyEarning>[]],
      );

      if (!mounted) return;

      final baseRateResponse = results[0];
      final grossAmountResponse = results[1];
      final earningsResponse = results[2];

      final baseRate = baseRateResponse != null
          ? (baseRateResponse as dynamic).data?.toDouble() ?? 0.0
          : 0.0;

      final earnings = earningsResponse is List<DailyEarning>
          ? earningsResponse
          : <DailyEarning>[];

      final hasValidData = baseRate > 0 || earnings.isNotEmpty;

      if (hasValidData) {
        _baseHourRate.value = baseRate;
        _weeklyEarnings.value = earnings;

        final isCurrentPeriod = _isCurrentPeriodData(earnings);
        _hasCurrentPeriodData.value = isCurrentPeriod;

        if (isCurrentPeriod && grossAmountResponse != null) {
          _weekGrossAmount.value = (grossAmountResponse as dynamic).data?.toDouble() ?? 0.0;
        } else {
          _weekGrossAmount.value = 0.0;
        }

        _updateCachedMaxEarning();

        await ViewDetailsCacheManager.saveCachedData(
          baseRate: _baseHourRate.value,
          grossAmount: _weekGrossAmount.value,
          weeklyEarnings: earnings,
        );

        print('Data loaded (grossAmount: ${grossAmountResponse != null ? "OK" : "FAILED"}) in ${stopwatch.elapsedMilliseconds}ms');
      } else {
        _hasCurrentPeriodData.value = false;
        print('No valid data received');
      }

      _isLoading.value = false;
      _isDataFromCache = false;
      stopwatch.stop();

    } catch (e) {
      print('Error loading data: $e (took ${stopwatch.elapsedMilliseconds}ms)');
      stopwatch.stop();

      if (mounted) {
        _isLoading.value = false;

        final cachedData = await ViewDetailsCacheManager.getCachedData();
        if (cachedData != null &&
            (cachedData['baseRate'] > 0 || (cachedData['weeklyEarnings'] as List).isNotEmpty)) {

          _baseHourRate.value = cachedData['baseRate'];
          _weekGrossAmount.value = cachedData['grossAmount'];
          _weeklyEarnings.value = cachedData['weeklyEarnings'];

          final isCurrentPeriod = _isCurrentPeriodData(cachedData['weeklyEarnings']);
          _hasCurrentPeriodData.value = isCurrentPeriod;
          _isDataFromCache = true;

          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Using cached data'),
                duration: Duration(seconds: 2),
              ),
            );
          }
        } else {
          _hasCurrentPeriodData.value = false;
        }
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
        actions: [
          IconButton(
            icon: Icon(Icons.refresh, color: textColor),
            onPressed: () async {
              setState(() => _isLoading.value = true);
              await _loadData(forceRefresh: true);
            },
            tooltip: 'Refresh data',
          ),
        ],
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
              if (!hasData) {
                return _buildNoDataView(textColor, subtitleColor);
              }

              return RefreshIndicator(
                onRefresh: () => _loadData(forceRefresh: true),
                child: SingleChildScrollView(
                  physics: const BouncingScrollPhysics(),
                  child: Padding(
                    padding: EdgeInsets.all(_padding),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
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
                        FadeTransition(
                          opacity: _fadeController,
                          child: _buildProductivityMetrics(textColor, subtitleColor, cardColor),
                        ),

                        SizedBox(height: _sectionSpacing),

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
              _l10n.get('noDataForCurrentPeriod'),
              style: GoogleFonts.poppins(
                fontSize: 20,
                fontWeight: FontWeight.w600,
                color: textColor,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              '${_l10n.get('period')}: $_cachedPeriod',
              style: GoogleFonts.poppins(
                fontSize: 14,
                color: subtitleColor,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              _l10n.get('noHoursRecordedThisWeek'),
              style: GoogleFonts.poppins(
                fontSize: 14,
                color: subtitleColor,
              ),
            ),
            const SizedBox(height: 32),
            ElevatedButton.icon(
              onPressed: () => Navigator.pop(context),
              icon: const Icon(Icons.arrow_back),
              label: Text(_l10n.get('goBack')),
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
                              _l10n.get('hours'),
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
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildStatRow(
                      icon: Icons.trending_up,
                      label: _l10n.get('overtime'),
                      value: '${overtimeHours.toStringAsFixed(1)}h',
                      color: Colors.green,
                    ),
                    const SizedBox(height: 12),
                    _buildStatRow(
                      icon: Icons.trending_down,
                      label: _l10n.get('missedHours'),
                      value: '${missedHours.toStringAsFixed(1)}h',
                      color: Colors.orange,
                    ),
                    const SizedBox(height: 12),
                    _buildStatRow(
                      icon: Icons.check_circle,
                      label: _l10n.get('status'),
                      value: displayHours >= 40 ? _l10n.get('complete') : _l10n.get('inProgress'),
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
          _l10n.get('productivityMetrics'),
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
                title: _l10n.get('productivity'),
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
                title: _l10n.get('efficiency'),
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
                _l10n.get('earningsOverview'),
                style: GoogleFonts.poppins(
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                  color: textColor,
                ),
              ),
            ],
          ),
          const SizedBox(height: 20),

          Row(
            children: [
              Expanded(
                child: _buildEarningMetric(
                  label: _l10n.get('hourlyRate'),
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
                  label: _l10n.get('weekTotal'),
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

          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    _l10n.get('earningProgress'),
                    style: GoogleFonts.poppins(
                      fontSize: 13,
                      color: subtitleColor,
                    ),
                  ),
                  Text(
                    '${actualVsProjected.toStringAsFixed(0)}% ${_l10n.get('ofTarget')}',
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
                _l10n.get('weeklyPerformance'),
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
                      _l10n.get('noDataAvailable'),
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
    final days = [
      _l10n.get('mon'),
      _l10n.get('tue'),
      _l10n.get('wed'),
      _l10n.get('thu'),
      _l10n.get('fri'),
      _l10n.get('sat'),
      _l10n.get('sun'),
    ];
    return days[date.weekday - 1];
  }
}