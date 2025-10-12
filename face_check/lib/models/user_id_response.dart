

class UserIdResponse {
  final int? userId;

  UserIdResponse({
    this.userId,
  });

  factory UserIdResponse.fromJson(Map<String, dynamic> json) {
    return UserIdResponse(
      userId: json['userId'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
    };
  }
}