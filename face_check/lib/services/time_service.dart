import 'dart:io';
import 'package:dio/dio.dart';
import 'package:timezone/timezone.dart' as tz;

class TimeService {
  final Dio dio;
  Duration _serverOffset = Duration.zero;
  late final tz.Location _ny;

  TimeService(this.dio) {
    _ny = tz.getLocation('America/New_York');
  }

  /// Синхронизация: берём HTTP-заголовок Date (UTC) и считаем offset
  Future<void> sync() async {
    try {
      // Используй лёгкий GET-эндпоинт, который у тебя всегда жив.
      // Можно любой: /actuator/health, /ping и т.п.
      final resp = await dio.get('actuator/health', options: Options(followRedirects: false));
      final dateHeader = resp.headers.value('date'); // пример: "Sun, 10 Aug 2025 17:13:21 GMT"
      if (dateHeader != null) {
        final serverUtc = HttpDate.parse(dateHeader).toUtc();
        final deviceUtc = DateTime.now().toUtc();
        _serverOffset = serverUtc.difference(deviceUtc);
      } else {
        _serverOffset = Duration.zero; // fallback
      }
    } catch (_) {
      _serverOffset = Duration.zero;   // fallback
    }
  }

  /// Текущее UTC с поправкой на сервер
  DateTime nowUtc() => DateTime.now().toUtc().add(_serverOffset);

  /// Нью-Йоркское время (учитывает DST)
  tz.TZDateTime nowNY() => tz.TZDateTime.from(nowUtc(), _ny);

  /// Тикер каждую секунду (NY-время)
  Stream<tz.TZDateTime> nyTicker() async* {
    yield nowNY(); // мгновенный первый тик
    yield* Stream.periodic(const Duration(seconds: 1), (_) => nowNY());
  }
}
