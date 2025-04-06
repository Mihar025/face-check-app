class PageResponse<T> {
  final List<T> content;
  final int pageNumber;
  final int pageSize;
  final int totalElements;
  final int totalPages;
  final bool first;
  final bool last;

  PageResponse({
    required this.content,
    required this.pageNumber,
    required this.pageSize,
    required this.totalElements,
    required this.totalPages,
    required this.first,
    required this.last,
  });

  factory PageResponse.fromJson(Map<String, dynamic> json,
      T Function(dynamic) fromJson) {
    return PageResponse(
      content: json['content'] != null
          ? (json['content'] as List).map((x) => fromJson(x)).toList()
          : [],
      pageNumber: json['pageNumber'] as int? ?? 0,
      pageSize: json['pageSize'] as int? ?? 0,
      totalElements: json['totalElements'] as int? ?? 0,
      totalPages: json['totalPages'] as int? ?? 0,
      first: json['first'] as bool? ?? true,
      last: json['last'] as bool? ?? true,
    );
  }
}