import 'daily_finance_info.dart';

class FinanceInfoResponse {
  final double totalHoursWorked;
  final double totalGrossPay;
  final double totalNetPay;
  final DateTime periodStart;
  final DateTime periodEnd;
  final List<DailyFinanceInfo> dailyInfo;

  FinanceInfoResponse({
    required this.totalHoursWorked,
    required this.totalGrossPay,
    required this.totalNetPay,
    required this.periodStart,
    required this.periodEnd,
    required this.dailyInfo,
  });

  factory FinanceInfoResponse.fromJson(Map<String, dynamic> json) {
    return FinanceInfoResponse(
      totalHoursWorked: json['totalHoursWorked']?.toDouble() ?? 0.0,
      totalGrossPay: json['totalGrossPay']?.toDouble() ?? 0.0,
      totalNetPay: json['totalNetPay']?.toDouble() ?? 0.0,
      periodStart: DateTime.parse(json['periodStart']),
      periodEnd: DateTime.parse(json['periodEnd']),
      dailyInfo: (json['dailyInfo'] as List)
          .map((item) => DailyFinanceInfo.fromJson(item))
          .toList(),
    );
  }
}