

class DateTimeFormatter {

  static String getMonthName(int month){
    const months = [
      'January', 'February', 'March', 'April', 'May',
      'June', 'July','August', 'September', 'October', 'November','December'
    ];
    return months[month - 1];
  }

  static String formateDate(DateTime dateTime){
    return '${dateTime.day} ${getMonthName(dateTime.month)}';
  }

  static String formatTime(DateTime dateTime) {
    return '${dateTime.hour.toString().padLeft(2, '0')}:'
        '${dateTime.minute.toString().padLeft(2, '0')}';
       // '${dateTime.second.toString().padLeft(2, '0')}';
  }






}