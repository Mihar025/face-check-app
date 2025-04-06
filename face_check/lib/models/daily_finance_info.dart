class DailyFinanceInfo {
  final DateTime date;
  final double hoursWorked;
  final double grossPay;

  DailyFinanceInfo({
    required this.date,
    required this.hoursWorked,
    required this.grossPay,
  });

  factory DailyFinanceInfo.fromJson(Map<String, dynamic> json) {
    return DailyFinanceInfo(
      date: DateTime.parse(json['date']),
      hoursWorked: json['hoursWorked']?.toDouble() ?? 0.0,
      grossPay: json['grossPay']?.toDouble() ?? 0.0,
    );
  }
}