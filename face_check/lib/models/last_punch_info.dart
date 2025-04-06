class LastPunchInfo {
  final String date;
  final String time;

  LastPunchInfo({required this.date, required this.time});

  factory LastPunchInfo.fromJson(Map<String, dynamic> json) {
    print("Received JSON: $json");
    return LastPunchInfo(
        date: json['formattedDate'] ?? 'DD/MM/YYYY',
        time: json['formattedTime'] ?? '--:--'
    );
  }
}