

class UpdatePunchInForWorkerResponse {
  final int workerId;
  final String current_work_site;
  final String workerFullName;
  final DateTime newPunchInTime;

  UpdatePunchInForWorkerResponse({
    required this.workerId,
    required this.current_work_site,
    required this.workerFullName,
    required this.newPunchInTime,
  });

  factory UpdatePunchInForWorkerResponse.fromJson(Map<String, dynamic> json) {
    DateTime parsedDateTime;
    if (json['newPunchInTime'] is String) {
      try {
        parsedDateTime = DateTime.parse(json['newPunchInTime']);
      } catch (e) {
        print('Error parsing datetime: ${e.toString()}');
        parsedDateTime = DateTime.now();
      }
    } else if (json['newPunchInTime'] is DateTime) {
      parsedDateTime = json['newPunchInTime'];
    } else {
      parsedDateTime = DateTime.now();
      print('Warning: newPunchInTime field is missing or invalid');
    }

    return UpdatePunchInForWorkerResponse(
      workerId: json['workerId'] as int,
      current_work_site: json['current_work_site'] as String? ?? '',
      workerFullName: json['workerFullName'] as String? ?? '',
      newPunchInTime: parsedDateTime,
    );
  }
}