class DailyEarning {
  final DateTime date;
  final double netPay;

  DailyEarning({
    required this.date,
    required this.netPay,
  });

  factory DailyEarning.fromJson(Map<String, dynamic> json) {
    return DailyEarning(
      date: DateTime.parse(json['date']),
      netPay: json['netPay'].toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'date': date.toIso8601String(),
    'netPay': netPay,
  };
}