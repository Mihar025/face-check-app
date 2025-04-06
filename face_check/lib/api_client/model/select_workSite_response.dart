class SelectWorkSiteResponse {
  final String selectedWorkSiteName;
  final int selectedWorkSiteId;
  final int workerId;

  SelectWorkSiteResponse({
    required this.selectedWorkSiteName,
    required this.selectedWorkSiteId,
    required this.workerId,
  });

  factory SelectWorkSiteResponse.fromJson(Map<String, dynamic> json) {
    return SelectWorkSiteResponse(
      selectedWorkSiteName: json['selectedWorkSiteName'],
      selectedWorkSiteId: json['selectedWorkSiteId'],
      workerId: json['workerId'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'selectedWorkSiteName': selectedWorkSiteName,
      'selectedWorkSiteId': selectedWorkSiteId,
      'workerId': workerId,
    };
  }
}