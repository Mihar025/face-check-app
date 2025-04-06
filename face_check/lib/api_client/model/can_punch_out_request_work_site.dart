import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

import 'local_time.dart';

part 'can_punch_out_request_work_site.g.dart';

@BuiltValue()
abstract class CanPunchOutRequestWorkSite implements Built<CanPunchOutRequestWorkSite, CanPunchOutRequestWorkSiteBuilder> {
  @BuiltValueField(wireName: r'canPunchOut')
  LocalTime get canPunchOut;

  CanPunchOutRequestWorkSite._();

  factory CanPunchOutRequestWorkSite([void Function(CanPunchOutRequestWorkSiteBuilder) updates]) = _$CanPunchOutRequestWorkSite;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CanPunchOutRequestWorkSiteBuilder b) => b;

  static Serializer<CanPunchOutRequestWorkSite> get serializer => _$canPunchOutRequestWorkSiteSerializer;
}