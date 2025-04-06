class WorksiteWorkerResponse {
  final int? workerId;
  final String firstName;
  final String lastName;
  final String? phoneNumber;
  final String? workSiteAddress;
  final DateTime? punchIn;

  WorksiteWorkerResponse({
    this.workerId,
    required this.firstName,
    required this.lastName,
    this.phoneNumber,
    this.workSiteAddress,
    this.punchIn,
  });

  factory WorksiteWorkerResponse.fromJson(Map<String, dynamic> json) {
    return WorksiteWorkerResponse(
      workerId: json['workerId'] as int?,
      firstName: json['firstName'] as String? ?? '',
      lastName: json['lastName'] as String? ?? '',
      phoneNumber: json['phoneNumber'] as String?,
      workSiteAddress: json['workSiteAddress'] as String?,
      punchIn: json['punchIn'] != null ? DateTime.parse(json['punchIn'] as String) : null,
    );
  }
}