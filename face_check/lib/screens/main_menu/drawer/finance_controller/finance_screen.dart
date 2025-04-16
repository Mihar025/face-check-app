import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:share_plus/share_plus.dart';
import '../../../../models/finance_info_response.dart';
import '../../../../services/pdf_service.dart';
import '../../../../services/ApiService.dart';
import '../../../../providers/localization_provider.dart';

class FinanceScreen extends StatefulWidget {
  const FinanceScreen({super.key});

  @override
  State<FinanceScreen> createState() => _FinanceScreenState();
}

class _FinanceScreenState extends State<FinanceScreen> with SingleTickerProviderStateMixin {
  late ApiService _apiService;
  DateTime currentWeekStart = DateTime.now();
  FinanceInfoResponse? financeInfo;
  bool isLoading = false;
  late AnimationController _animationController;

  @override
  void initState() {
    super.initState();
    _apiService = ApiService.instance;
    _animationController = AnimationController(
      duration: const Duration(milliseconds: 300),
      vsync: this,
    );

    currentWeekStart = DateTime.now().subtract(
      Duration(days: DateTime.now().weekday),
    );
    fetchFinanceInfo();
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  Future<void> fetchFinanceInfo() async {
    setState(() => isLoading = true);
    try {
      final response = await _apiService.getFinanceInfo(currentWeekStart);
      setState(() => financeInfo = response);
      _animationController.forward(from: 0.0);
    } catch (e) {
      if (mounted) {
        final l10n = context.read<LocalizationProvider>().localizations;
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('${l10n.get('finance.errorLoadingData')}: $e'),
            backgroundColor: Colors.red.shade400,
            behavior: SnackBarBehavior.floating,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          ),
        );
      }
    } finally {
      setState(() => isLoading = false);
    }
  }

  void changeWeek(int days) {
    setState(() {
      currentWeekStart = currentWeekStart.add(Duration(days: days));
    });
    fetchFinanceInfo();
  }

  String formatDate(DateTime date) {
    final weekDay = DateFormat('EEE').format(date);
    final dateStr = DateFormat('MM/dd').format(date);
    return '$weekDay\n$dateStr';
  }

  String formatMoney(double amount) {
    return '\$${amount.toStringAsFixed(2)}';
  }

  double calculateTaxes() {
    if (financeInfo == null) return 0.0;
    return financeInfo!.totalGrossPay - financeInfo!.totalNetPay;
  }

  Future<void> _downloadFinanceReport() async {
    if (financeInfo != null) {
      final l10n = context.read<LocalizationProvider>().localizations;

      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (context) => AlertDialog(
          content: Row(
            children: [
              const CircularProgressIndicator(color: Colors.blue),
              const SizedBox(width: 24),
              Text(l10n.get('finance.downloadPdfReport')),
            ],
          ),
        ),
      );

      try {
        await FinancePdfService.generateFinanceReport(financeInfo!);
        Navigator.of(context).pop();
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(l10n.get('finance.reportDownloadedSuccessfully')),
            backgroundColor: Colors.green,
            behavior: SnackBarBehavior.floating,
          ),
        );
      } catch (e) {
        Navigator.of(context).pop();
        print('Error saving PDF: $e');
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('${l10n.get('finance.errorDownloadingReport')}: $e'),
            backgroundColor: Colors.red.shade400,
            behavior: SnackBarBehavior.floating,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;

    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    String periodText = '${DateFormat('MM/dd/yy').format(currentWeekStart)} - ${DateFormat('MM/dd/yy').format(currentWeekStart.add(const Duration(days: 6)))}';

    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        elevation: 0,
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            color: Colors.white,
            size: isSmallScreen ? 20 : 24,
          ),
          onPressed: () => Navigator.of(context).pop(),
        ),
        title: Text(
          l10n.get('finance.title'),
          style: TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.w600,
            fontSize: isSmallScreen ? 20 : 22,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(
              Icons.download,
              color: Colors.white,
              size: isSmallScreen ? 20 : 24,
            ),
            tooltip: l10n.get('finance.downloadTooltip'),
            onPressed: _downloadFinanceReport,
          ),
          IconButton(
            icon: Icon(
              Icons.share,
              color: Colors.white,
              size: isSmallScreen ? 20 : 24,
            ),
            tooltip: l10n.get('finance.shareTooltip'),
            onPressed: () {
              if (financeInfo != null) {
                Share.share(
                  '${l10n.get('finance.shareTitle')} (${periodText}):\n'
                      '${l10n.get('finance.totalHours')}: ${financeInfo?.totalHoursWorked.toStringAsFixed(1) ?? "0.0"}\n'
                      '${l10n.get('finance.totalGrossPay')}: ${formatMoney(financeInfo?.totalGrossPay ?? 0.0)}\n'
                      '${l10n.get('finance.totalNetPay')}: ${formatMoney(financeInfo?.totalNetPay ?? 0.0)}\n'
                      '${l10n.get('finance.totalTaxes')}: ${formatMoney(calculateTaxes())}',
                );
              }
            },
          ),
        ],
      ),
      body: Column(
        children: [
          Container(
            padding: EdgeInsets.symmetric(
                horizontal: isSmallScreen ? 12.0 : 16.0,
                vertical: isSmallScreen ? 10.0 : 12.0
            ),
            color: Colors.blue.shade50,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                TextButton.icon(
                  onPressed: () => changeWeek(-7),
                  icon: Icon(
                      Icons.arrow_back_ios,
                      size: isSmallScreen ? 14 : 16,
                      color: Colors.blue
                  ),
                  label: Text(
                    l10n.get('finance.previous'),
                    style: TextStyle(
                      color: Colors.blue,
                      fontWeight: FontWeight.w500,
                      fontSize: isSmallScreen ? 12 : 14,
                    ),
                  ),
                ),
                Column(
                  children: [
                    Text(
                      l10n.get('finance.weekPeriod'),
                      style: TextStyle(
                        fontSize: isSmallScreen ? 10 : 12,
                        color: Colors.blue.shade700,
                      ),
                    ),
                    SizedBox(height: isSmallScreen ? 2 : 4),
                    Text(
                      periodText,
                      style: TextStyle(
                        fontSize: isSmallScreen ? 12 : 14,
                        fontWeight: FontWeight.w600,
                        color: Colors.blue.shade700,
                      ),
                    ),
                  ],
                ),
                TextButton.icon(
                  onPressed: () => changeWeek(7),
                  label: Text(
                    l10n.get('finance.next'),
                    style: TextStyle(
                      color: Colors.blue,
                      fontWeight: FontWeight.w500,
                      fontSize: isSmallScreen ? 12 : 14,
                    ),
                  ),
                  icon: Icon(
                      Icons.arrow_forward_ios,
                      size: isSmallScreen ? 14 : 16,
                      color: Colors.blue
                  ),
                ),
              ],
            ),
          ),

          if (!isLoading && financeInfo != null)
            Padding(
              padding: EdgeInsets.all(isSmallScreen ? 12 : 16),
              child: Container(
                padding: EdgeInsets.all(isSmallScreen ? 12 : 16),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.grey.shade200),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _buildSummaryItem(
                      l10n.get('finance.hours'),
                      '${financeInfo?.totalHoursWorked.toStringAsFixed(1) ?? "0.0"}',
                      Icons.access_time_rounded,
                      isSmallScreen,
                    ),
                    _buildSummaryItem(
                      l10n.get('finance.gross'),
                      formatMoney(financeInfo?.totalGrossPay ?? 0.0),
                      Icons.trending_up,
                      isSmallScreen,
                    ),
                    _buildSummaryItem(
                      l10n.get('finance.net'),
                      formatMoney(financeInfo?.totalNetPay ?? 0.0),
                      Icons.account_balance_wallet,
                      isSmallScreen,
                    ),
                  ],
                ),
              ),
            ),

          Expanded(
            child: isLoading
                ? const Center(
              child: CircularProgressIndicator(
                color: Colors.blue,
              ),
            )
                : Container(
              margin: EdgeInsets.fromLTRB(
                  isSmallScreen ? 12 : 16,
                  0,
                  isSmallScreen ? 12 : 16,
                  isSmallScreen ? 12 : 16
              ),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Column(
                children: [
                  // Table Header
                  Container(
                    padding: EdgeInsets.symmetric(
                      horizontal: isSmallScreen ? 8.0 : 12.0,
                      vertical: isSmallScreen ? 10.0 : 12.0,
                    ),
                    decoration: BoxDecoration(
                      color: Colors.blue.shade50,
                      borderRadius: const BorderRadius.vertical(
                        top: Radius.circular(8),
                      ),
                    ),
                    child: Row(
                      children: [
                        SizedBox(
                          width: isSmallScreen ? 80 : 100,
                          child: Text(
                            l10n.get('finance.day'),
                            style: TextStyle(
                              fontWeight: FontWeight.w600,
                              color: Colors.blue,
                              fontSize: isSmallScreen ? 12 : 14,
                            ),
                          ),
                        ),
                        SizedBox(width: isSmallScreen ? 6 : 8),
                        Expanded(
                          child: Text(
                            l10n.get('finance.hours'),
                            style: TextStyle(
                              fontWeight: FontWeight.w600,
                              color: Colors.blue,
                              fontSize: isSmallScreen ? 12 : 14,
                            ),
                          ),
                        ),
                        Expanded(
                          child: Text(
                            l10n.get('finance.gross'),
                            style: TextStyle(
                              fontWeight: FontWeight.w600,
                              color: Colors.blue,
                              fontSize: isSmallScreen ? 12 : 14,
                            ),
                          ),
                        ),
                        Expanded(
                          child: Text(
                            l10n.get('finance.net'),
                            style: TextStyle(
                              fontWeight: FontWeight.w600,
                              color: Colors.blue,
                              fontSize: isSmallScreen ? 12 : 14,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),

                  // Daily Info
                  Expanded(
                    child: financeInfo?.dailyInfo.isEmpty ?? true
                        ? Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            Icons.event_busy,
                            size: isSmallScreen ? 32 : 40,
                            color: Colors.grey.shade400,
                          ),
                          SizedBox(height: isSmallScreen ? 12 : 16),
                          Text(
                            l10n.get('finance.noDataForPeriod'),
                            style: TextStyle(
                              fontSize: isSmallScreen ? 14 : 16,
                              color: Colors.grey.shade600,
                            ),
                          ),
                          SizedBox(height: isSmallScreen ? 6 : 8),
                          TextButton(
                            onPressed: fetchFinanceInfo,
                            child: Text(
                              l10n.get('finance.refresh'),
                              style: TextStyle(
                                fontSize: isSmallScreen ? 13 : 14,
                              ),
                            ),
                          ),
                        ],
                      ),
                    )
                        : ListView.builder(
                      itemCount: (financeInfo?.dailyInfo.length ?? 0) + 1,
                      itemBuilder: (context, index) {
                        if (index == financeInfo!.dailyInfo.length) {
                          return Container(
                            padding: EdgeInsets.symmetric(
                              horizontal: isSmallScreen ? 12.0 : 16.0,
                              vertical: isSmallScreen ? 10.0 : 12.0,
                            ),
                            color: Colors.grey.shade50,
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(
                                  '${l10n.get('finance.totals')}:',
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: isSmallScreen ? 12 : 14,
                                  ),
                                ),
                                Text(
                                  '${financeInfo?.totalHoursWorked.toStringAsFixed(1) ?? "0.0"}',
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: isSmallScreen ? 12 : 14,
                                  ),
                                ),
                                Text(
                                  formatMoney(financeInfo?.totalGrossPay ?? 0.0),
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: isSmallScreen ? 12 : 14,
                                  ),
                                ),
                                Text(
                                  formatMoney(financeInfo?.totalNetPay ?? 0.0),
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: isSmallScreen ? 12 : 14,
                                  ),
                                ),
                              ],
                            ),
                          );
                        }

                        final daily = financeInfo!.dailyInfo[index];
                        return SlideTransition(
                          position: Tween<Offset>(
                            begin: const Offset(0.05, 0),
                            end: Offset.zero,
                          ).animate(CurvedAnimation(
                            parent: _animationController,
                            curve: Interval(
                              index * 0.1,
                              0.5 + index * 0.1,
                              curve: Curves.easeOut,
                            ),
                          )),
                          child: Container(
                            padding: EdgeInsets.symmetric(
                              horizontal: isSmallScreen ? 8.0 : 12.0,
                              vertical: isSmallScreen ? 10.0 : 12.0,
                            ),
                            decoration: BoxDecoration(
                              border: Border(
                                bottom: BorderSide(
                                  color: Colors.grey.shade200,
                                  width: 0.5,
                                ),
                              ),
                              color: index % 2 == 0
                                  ? Colors.white
                                  : Colors.grey.shade50,
                            ),
                            child: Row(
                              children: [
                                SizedBox(
                                  width: isSmallScreen ? 80 : 100,
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        DateFormat('EEE')
                                            .format(daily.date)
                                            .toUpperCase(),
                                        style: TextStyle(
                                          fontWeight: FontWeight.w600,
                                          color: Colors.blue.shade700,
                                          fontSize: isSmallScreen ? 12 : 14,
                                        ),
                                      ),
                                      SizedBox(height: isSmallScreen ? 1 : 2),
                                      Text(
                                        DateFormat('MM/dd').format(daily.date),
                                        style: TextStyle(
                                          color: Colors.grey.shade600,
                                          fontSize: isSmallScreen ? 10 : 12,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                                SizedBox(width: isSmallScreen ? 6 : 8),
                                Expanded(
                                  child: Text(
                                    daily.hoursWorked.toStringAsFixed(1),
                                    style: TextStyle(
                                      color: daily.hoursWorked > 0
                                          ? Colors.black87
                                          : Colors.grey,
                                      fontSize: isSmallScreen ? 12 : 14,
                                    ),
                                  ),
                                ),
                                Expanded(
                                  child: Text(
                                    formatMoney(daily.grossPay),
                                    style: TextStyle(
                                      color: daily.grossPay > 0
                                          ? Colors.black87
                                          : Colors.grey,
                                      fontSize: isSmallScreen ? 12 : 14,
                                    ),
                                  ),
                                ),
                                Expanded(
                                  child: Text(
                                    formatMoney(financeInfo?.totalNetPay ?? 0.0),
                                    style: TextStyle(
                                      color: daily.grossPay > 0
                                          ? Colors.black87
                                          : Colors.grey,
                                      fontSize: isSmallScreen ? 12 : 14,
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSummaryItem(String label, String value, IconData icon, bool isSmallScreen) {
    return Column(
      children: [
        Icon(
            icon,
            color: Colors.blue.shade600,
            size: isSmallScreen ? 18 : 20
        ),
        SizedBox(height: isSmallScreen ? 6 : 8),
        Text(
          label,
          style: TextStyle(
            color: Colors.grey.shade600,
            fontSize: isSmallScreen ? 10 : 12,
          ),
        ),
        SizedBox(height: isSmallScreen ? 3 : 4),
        Text(
          value,
          style: TextStyle(
            fontSize: isSmallScreen ? 14 : 16,
            fontWeight: FontWeight.w600,
            color: Colors.blue.shade800,
          ),
        ),
      ],
    );
  }
}