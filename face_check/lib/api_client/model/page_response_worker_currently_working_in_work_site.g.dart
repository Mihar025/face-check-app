// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_worker_currently_working_in_work_site.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseWorkerCurrentlyWorkingInWorkSite
    extends PageResponseWorkerCurrentlyWorkingInWorkSite {
  @override
  final BuiltList<WorkerCurrentlyWorkingInWorkSite>? content;
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

  factory _$PageResponseWorkerCurrentlyWorkingInWorkSite(
          [void Function(PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder)?
              updates]) =>
      (new PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder()
            ..update(updates))
          ._build();

  _$PageResponseWorkerCurrentlyWorkingInWorkSite._(
      {this.content,
      this.number,
      this.size,
      this.totalElement,
      this.totalPages,
      this.first,
      this.last})
      : super._();

  @override
  PageResponseWorkerCurrentlyWorkingInWorkSite rebuild(
          void Function(PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder toBuilder() =>
      new PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseWorkerCurrentlyWorkingInWorkSite &&
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
            r'PageResponseWorkerCurrentlyWorkingInWorkSite')
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

class PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder
    implements
        Builder<PageResponseWorkerCurrentlyWorkingInWorkSite,
            PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder> {
  _$PageResponseWorkerCurrentlyWorkingInWorkSite? _$v;

  ListBuilder<WorkerCurrentlyWorkingInWorkSite>? _content;
  ListBuilder<WorkerCurrentlyWorkingInWorkSite> get content =>
      _$this._content ??= new ListBuilder<WorkerCurrentlyWorkingInWorkSite>();
  set content(ListBuilder<WorkerCurrentlyWorkingInWorkSite>? content) =>
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

  PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder() {
    PageResponseWorkerCurrentlyWorkingInWorkSite._defaults(this);
  }

  PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder get _$this {
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
  void replace(PageResponseWorkerCurrentlyWorkingInWorkSite other) {
    ArgumentError.checkNotNull(other, 'other');
    _$v = other as _$PageResponseWorkerCurrentlyWorkingInWorkSite;
  }

  @override
  void update(
      void Function(PageResponseWorkerCurrentlyWorkingInWorkSiteBuilder)?
          updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseWorkerCurrentlyWorkingInWorkSite build() => _build();

  _$PageResponseWorkerCurrentlyWorkingInWorkSite _build() {
    _$PageResponseWorkerCurrentlyWorkingInWorkSite _$result;
    try {
      _$result = _$v ??
          new _$PageResponseWorkerCurrentlyWorkingInWorkSite._(
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
            r'PageResponseWorkerCurrentlyWorkingInWorkSite',
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
