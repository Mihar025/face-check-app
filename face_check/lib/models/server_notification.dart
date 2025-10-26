// models/server_notification.dart
class ServerNotification {
  final String message;
  final DateTime createdAt;

  ServerNotification({
    required this.message,
    required this.createdAt,
  });

  factory ServerNotification.fromJson(Map<String, dynamic> json) {
    return ServerNotification(
      message: json['message'] ?? '',
      createdAt: DateTime.parse(json['createdAt'] ?? DateTime.now().toIso8601String()),
    );
  }
}

class NotificationsPageResponse {
  final List<ServerNotification> content;
  final int totalElement;
  final int totalPages;

  NotificationsPageResponse({
    required this.content,
    required this.totalElement,
    required this.totalPages,
  });

  factory NotificationsPageResponse.fromJson(Map<String, dynamic> json) {
    return NotificationsPageResponse(
      content: (json['content'] as List?)
          ?.map((item) => ServerNotification.fromJson(item))
          .toList() ?? [],
      totalElement: json['totalElement'] ?? 0,
      totalPages: json['totalPages'] ?? 0,
    );
  }

  factory NotificationsPageResponse.empty() {
    return NotificationsPageResponse(
      content: [],
      totalElement: 0,
      totalPages: 0,
    );
  }
}