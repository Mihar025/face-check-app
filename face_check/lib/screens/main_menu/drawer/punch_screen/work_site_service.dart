import 'package:dio/dio.dart';

import '../../../../../api_client/api/work_site_controller_api.dart';
import '../../../../../api_client/model/local_time.dart';
import '../../../../../api_client/model/work_site_response.dart';
import '../../../../../api_client/serializers.dart';


class WorkSiteService {
  final Dio dio;

  WorkSiteService(this.dio);

  Future<List<WorkSiteResponse>> loadWorkSites() async {
    try {
      final response = await dio.get('http://192.168.1.194:8088/api/v1/workSite', queryParameters: {
        'page': 0,
        'size': 10,
      });

      if (response.data != null) {
        final Map<String, dynamic> responseData = response.data;
        final List<dynamic> content = responseData['content'] as List;

        return content.map((item) {
          final startTime = LocalTime((b) => b
            ..hour = int.parse(item['workDayStart'].split(':')[0])
            ..minute = int.parse(item['workDayStart'].split(':')[1])
            ..second = 0);

          final endTime = LocalTime((b) => b
            ..hour = int.parse(item['workDayEnd'].split(':')[0])
            ..minute = int.parse(item['workDayEnd'].split(':')[1])
            ..second = 0);

          return WorkSiteResponse((b) => b
            ..workSiteId = item['workSiteId']
            ..workSiteName = item['workSiteName']
            ..address = item['address']
            ..latitude = (item['latitude'] as num).toDouble()
            ..longitude = (item['longitude'] as num).toDouble()
            ..allowedRadius = (item['allowedRadius'] as num).toDouble()
            ..workDayStart = startTime.toBuilder()
            ..workDayEnd = endTime.toBuilder()
          );
        }).toList();
      }
      return [];
    } catch (e) {
      print('Error loading work sites: $e');
      rethrow;
    }
  }

  Future<void> selectWorkSite(int workSiteId) async {
    final api = WorkSiteControllerApi(dio, serializers);
    await api.selectWorkSite(workSiteId: workSiteId);
  }
}