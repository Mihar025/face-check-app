class DateFormatter {
  static String _getMonthName(int month) {
    const months = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return months[month - 1];
  }

  static String getCurrentPeriod() {
    final now = DateTime.now();

    final startOfWeek = now.subtract(Duration(days: now.weekday % 7));
    final endOfWeek = startOfWeek.add(const Duration(days: 6));

    final startMonth = _getMonthName(startOfWeek.month);
    final endMonth = _getMonthName(endOfWeek.month);

    if (startOfWeek.month == endOfWeek.month) {
      return '$startMonth ${startOfWeek.day} - ${endOfWeek.day}';
    } else {
      return '$startMonth ${startOfWeek.day} - $endMonth ${endOfWeek.day}, ${startOfWeek.year}';
    }
  }
}