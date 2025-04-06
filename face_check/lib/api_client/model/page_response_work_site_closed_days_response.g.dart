// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_work_site_closed_days_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseWorkSiteClosedDaysResponse
    extends PageResponseWorkSiteClosedDaysResponse {
  @override
  final BuiltList<WorkSiteClosedDaysResponse>? content;
  @override
  final int? number;
  @override
  final int? size;
  @override
  final int? totalElement;
  @override
  final int? totalPages;
  @override
  final bool? first;
  @override
  final bool? last;

  factory _$PageResponseWorkSiteClosedDaysResponse(
          [void Function(PageResponseWorkSiteClosedDaysResponseBuilder)?
              updates]) =>
      (new PageResponseWorkSiteClosedDaysResponseBuilder()..update(updates))
          ._build();

  _$PageResponseWorkSiteClosedDaysResponse._(
      {this.content,
      this.number,
      this.size,
      this.totalElement,
      this.totalPages,
      this.first,
      this.last})
      : super._();

  @override
  PageResponseWorkSiteClosedDaysResponse rebuild(
          void Function(PageResponseWorkSiteClosedDaysResponseBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseWorkSiteClosedDaysResponseBuilder toBuilder() =>
      new PageResponseWorkSiteClosedDaysResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseWorkSiteClosedDaysResponse &&
        content == other.content &&
        number == other.number &&
        size == other.size &&
        totalElement == other.totalElement &&
        totalPages == other.totalPages &&
        first == other.first &&
        last == other.last;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, content.hashCode);
    _$hash = $jc(_$hash, number.hashCode);
    _$hash = $jc(_$hash, size.hashCode);
    _$hash = $jc(_$hash, totalElement.hashCode);
    _$hash = $jc(_$hash, totalPages.hashCode);
    _$hash = $jc(_$hash, first.hashCode);
    _$hash = $jc(_$hash, last.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(
            r'PageResponseWorkSiteClosedDaysResponse')
          ..add('content', content)
          ..add('number', number)
          ..add('size', size)
          ..add('totalElement', totalElement)
          ..add('totalPages', totalPages)
          ..add('first', first)
          ..add('last', last))
        .toString();
  }
}

class PageResponseWorkSiteClosedDaysResponseBuilder
    implements
        Builder<PageResponseWorkSiteClosedDaysResponse,
            PageResponseWorkSiteClosedDaysResponseBuilder> {
  _$PageResponseWorkSiteClosedDaysResponse? _$v;

  ListBuilder<WorkSiteClosedDaysResponse>? _content;
  ListBuilder<WorkSiteClosedDaysResponse> get content =>
      _$this._content ??= new ListBuilder<WorkSiteClosedDaysResponse>();
  set content(ListBuilder<WorkSiteClosedDaysResponse>? content) =>
      _$this._content = content;

  int? _number;
  int? get number => _$this._number;
  set number(int? number) => _$this._number = number;

  int? _size;
  int? get size => _$this._size;
  set size(int? size) => _$this._size = size;

  int? _totalElement;
  int? get totalElement => _$this._totalElement;
  set totalElement(int? totalElement) => _$this._totalElement = totalElement;

  int? _totalPages;
  int? get totalPages => _$this._totalPages;
  set totalPages(int? totalPages) => _$this._totalPages = totalPages;

  bool? _first;
  bool? get first => _$this._first;
  set first(bool? first) => _$this._first = first;

  bool? _last;
  bool? get last => _$this._last;
  set last(bool? last) => _$this._last = last;

  PageResponseWorkSiteClosedDaysResponseBuilder() {
    PageResponseWorkSiteClosedDaysResponse._defaults(this);
  }

  PageResponseWorkSiteClosedDaysResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _content = $v.content?.toBuilder();
      _number = $v.number;
      _size = $v.size;
      _totalElement = $v.totalElement;
      _totalPages = $v.totalPages;
      _first = $v.first;
      _last = $v.last;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PageResponseWorkSiteClosedDaysResponse other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$PageResponseWorkSiteClosedDaysResponse;
  }

  @override
  void update(
      void Function(PageResponseWorkSiteClosedDaysResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseWorkSiteClosedDaysResponse build() => _build();

  _$PageResponseWorkSiteClosedDaysResponse _build() {
    _$PageResponseWorkSiteClosedDaysResponse _$result;
    try {
      _$result = _$v ??
          new _$PageResponseWorkSiteClosedDaysResponse._(
              content: _content?.build(),
              number: number,
              size: size,
              totalElement: totalElement,
              totalPages: totalPages,
              first: first,
              last: last);
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        _content?.build();
      } catch (e) {
        throw new BuiltValueNestedFieldError(
            r'PageResponseWorkSiteClosedDaysResponse',
            _$failedField,
            e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
