


class TermsOfUseRequest {
  final String event;
  final int userId;
  final String termsVersion;
  final String privacyVersion;
  final String ip;
  final String device;
  final String osVersion;

  TermsOfUseRequest({
    required this.event,
    required this.userId,
    required this.termsVersion,
    required this.privacyVersion,
    required this.ip,
    required this.device,
    required this.osVersion
  });


  Map<String, dynamic> toJson() => {
    'event': event,
    'userId': userId,
    'termsVersion': termsVersion,
    'privacyVersion': privacyVersion,
    'ip': ip,
    'device': device,
    'osVersion': osVersion,
    };
}


class TermsOfUseResponse {
  final int id;
  final String event;
  final int userId;
  final String timeStamp;
  final String termsVersion;
  final String privacyVersion;
  final String ip;
  final String device;
  final String osVersion;

  TermsOfUseResponse({
    required this.id,
    required this.event,
    required this.userId,
    required this.timeStamp,
    required this.termsVersion,
    required this.privacyVersion,
    required this.ip,
    required this.device,
    required this.osVersion
  });

  factory TermsOfUseResponse.fromJson(Map<String, dynamic> json){
    return TermsOfUseResponse(
      id: json['id'],
      event: json['event'],
      userId: json['userId'],
      timeStamp: json['timeStamp'],
      termsVersion: json['termsVersion'],
      privacyVersion: json['privacyVersion'],
      ip: json['ip'],
      device: json['device'],
      osVersion: json['osVersion']
    );
  }
}