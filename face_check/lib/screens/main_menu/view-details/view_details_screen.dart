import 'package:face_check/localization/app_localizations.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';
import '../../../models/daily_earnings.dart';
import '../../../providers/localization_provider.dart';
import '../../../services/ApiService.dart';
import 'components/info_row.dart';
import 'components/progress_circle/progress_circle.dart';
import 'components/progress_circle/payroll_progress_circle.dart';
import 'utils/date_formatter.dart';

class ViewDetailsScreen extends StatefulWidget {
  final double workedHours;

  const ViewDetailsScreen({
    super.key,
    required this.workedHours,
  });

  @override
  State<ViewDetailsScreen> createState() => _ViewDetailsScreenState();
}

class _ViewDetailsScreenState extends State<ViewDetailsScreen> {
  bool _isLoading = true;
  double _baseHourRate = 0.0;
  double _weekGrossAmount = 0.0;
  double _weekTaxesAmount = 0.0;
  double _weekNetAmount = 0.0;
  List<DailyEarning> _weeklyEarnings = [];

  static const double _largeSpacing = 40.0;
  static const double _smallSpacing = 30.0;
  static const double _largeFontSize = 18.0;
  static const double _smallFontSize = 16.0;
  static const double _largeRowSpacing = 12.0;
  static const double _smallRowSpacing = 8.0;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    try {
      final futures = await Future.wait([
        ApiService.instance.userApi.findWorkerBaseHourRate(),
        ApiService.instance.userApi.findWorkerSalaryPerWeekGross(),
        ApiService.instance.userApi.findWorkerTotalPayedTaxesAmountForWeek(),
        ApiService.instance.userApi.findWorkerSalaryPerWeekNet(),
        ApiService.instance.getWeeklyEarnings(),
      ]);

      if (mounted) {
        setState(() {
          _baseHourRate = (futures[0] as dynamic).data?.toDouble() ?? 0.0;
          _weekGrossAmount = (futures[1] as dynamic).data?.toDouble() ?? 0.0;
          _weekTaxesAmount = (futures[2] as dynamic).data?.toDouble() ?? 0.0;
          _weekNetAmount = (futures[3] as dynamic).data?.toDouble() ?? 0.0;
          _weeklyEarnings = futures[4] as List<DailyEarning>;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  double _getMaxEarning() => _weeklyEarnings.isEmpty
      ? 100.0
      : _weeklyEarnings.map((e) => e.netPay).reduce((a, b) => a > b ? a : b);

  String _formatCurrency(double value) => '\$${value.toStringAsFixed(2)}';

  @override
  Widget build(BuildContext context) {
    final l10n = context.read<LocalizationProvider>().localizations;
    final overtimeHours = widget.workedHours > 40 ? widget.workedHours - 40 : 0.0;
    final missedHours = widget.workedHours < 40 ? 40 - widget.workedHours : 0.0;

                       final isSmallScreen = MediaQuery.of(context).size.width < 400;
    final padding = isSmallScreen ? 12.0 : 16.0;
    final sectionSpacing = isSmallScreen ? _smallSpacing : _largeSpacing;

    if (_isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(
          l10n.get('productivity'),
          style: GoogleFonts.poppins(fontWeight: FontWeight.w600),
        ),
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: EdgeInsets.all(padding),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildSection(
                l10n,
                'attendance',
                isSmallScreen,
                firstWidget: ProgressCircle(workedHours: widget.workedHours),
                infoRows: [
                  _buildInfoRow(l10n, 'hours', '${widget.workedHours.toStringAsFixed(1)}hrs', Colors.orange, isSmallScreen),
                  _buildInfoRow(l10n, 'overtime', '${overtimeHours.toStringAsFixed(1)}hrs', Colors.red, isSmallScreen),
                  _buildInfoRow(l10n, 'missedHours', '${missedHours.toStringAsFixed(1)}hrs', Colors.yellow, isSmallScreen),
                  _buildInfoRow(l10n, 'period', DateFormatter.getCurrentPeriod(), Colors.green, isSmallScreen),
                ],
              ),

              SizedBox(height: sectionSpacing),

              _buildSection(
                l10n,
                'workerPayroll',
                isSmallScreen,
                firstWidget: PayrollProgressCircle(
                  baseRate: _baseHourRate,
                  grossAmount: _weekGrossAmount,
                  taxesAmount: _weekTaxesAmount,
                  netAmount: _weekNetAmount,
                  maxAmount: _weekGrossAmount > 0 ? _weekGrossAmount : 100,
                ),
                infoRows: [
                  _buildInfoRow(l10n, 'hourlyRate', _formatCurrency(_baseHourRate), Colors.purpleAccent, isSmallScreen),
                  _buildInfoRow(l10n, 'gross', _formatCurrency(_weekGrossAmount), Colors.indigo, isSmallScreen),
                  _buildInfoRow(l10n, 'totalTaxes', _formatCurrency(_weekTaxesAmount), Colors.lightBlue, isSmallScreen),
                  _buildInfoRow(l10n, 'netTotal', _formatCurrency(_weekNetAmount), Colors.indigoAccent, isSmallScreen),
                ],
                circleWidth: isSmallScreen ? 140.0 : 150.0,
                topPadding: 30.0,
              ),

              SizedBox(height: sectionSpacing),

              Padding(
                padding: EdgeInsets.only(left: isSmallScreen ? 8.0 : 12.0),
                child: Text(
                  l10n.get('workerFinancialStatistics'),
                  style: _getSectionStyle(isSmallScreen),
                ),
              ),
              SizedBox(height: isSmallScreen ? 16 : 24),
              _buildFinancialChart(l10n, isSmallScreen),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildSection(
      AppLocalizations l10n,
      String titleKey,
      bool isSmallScreen, {
        required Widget firstWidget,
        required List<Widget> infoRows,
        double? circleWidth,
        double topPadding = 59.0,
      }) {
    final titlePadding = isSmallScreen ? 8.0 : 12.0;
    final titleWidget = Padding(
      padding: EdgeInsets.only(left: titlePadding),
      child: Text(
        l10n.get(titleKey),
        style: _getSectionStyle(isSmallScreen),
      ),
    );

    final spacingAfterTitle = isSmallScreen ? 16.0 : 24.0;

    if (isSmallScreen) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          titleWidget,
          SizedBox(height: spacingAfterTitle),
          Center(
            child: circleWidth != null
                ? SizedBox(width: circleWidth, child: firstWidget)
                : firstWidget,
          ),
          SizedBox(height: 24),
          ...infoRows,
        ],
      );
    }

    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            titleWidget,
            SizedBox(height: spacingAfterTitle),
            if (circleWidth != null)
              SizedBox(width: circleWidth, child: firstWidget)
            else
              firstWidget,
          ],
        ),
        const SizedBox(width: 20),
        Expanded(
          child: Container(
            padding: EdgeInsets.only(top: topPadding),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: infoRows,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildInfoRow(
      AppLocalizations l10n,
      String labelKey,
      String value,
      Color color,
      bool isSmallScreen,
      ) {
    final spacing = isSmallScreen ? _smallRowSpacing : _largeRowSpacing;
    return Padding(
      padding: EdgeInsets.only(bottom: spacing),
      child: InfoRow(
        label: l10n.get(labelKey),
        value: value,
        color: color,
      ),
    );
  }

  TextStyle _getSectionStyle(bool isSmallScreen) {
    return GoogleFonts.poppins(
      fontSize: isSmallScreen ? _smallFontSize : _largeFontSize,
      fontWeight: FontWeight.w600,
      color: Colors.grey[800],
    );
  }

  Widget _buildFinancialChart(AppLocalizations l10n, bool isSmallScreen) {
    final height = isSmallScreen ? 280.0 : 320.0;
    final padding = isSmallScreen ? 12.0 : 16.0;
    final titleSize = isSmallScreen ? 14.0 : 16.0;
    final spacingAfterTitle = isSmallScreen ? 16.0 : 24.0;

    return Container(
      width: double.infinity,
      height: height,
      margin: EdgeInsets.symmetric(vertical: isSmallScreen ? 12 : 16),
      decoration: BoxDecoration(
        color: Colors.black87,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.2),
            spreadRadius: 2,
            blurRadius: 5,
            offset: const Offset(0, 3),
          ),
        ],
      ),
      child: Padding(
        padding: EdgeInsets.all(padding),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              l10n.get('earningsDynamics'),
              style: GoogleFonts.poppins(
                fontSize: titleSize,
                fontWeight: FontWeight.w600,
                color: Colors.white,
              ),
            ),
            SizedBox(height: spacingAfterTitle),
            Expanded(
              child: _weeklyEarnings.isEmpty
                  ? Center(
                child: Text(
                  l10n.get('noDataAvailable'),
                  style: const TextStyle(color: Colors.white),
                ),
              )
                  : _buildOptimizedChart(isSmallScreen),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildOptimizedChart(bool isSmallScreen) {
    final maxValue = _getMaxEarning();

    return CustomPaint(
      painter: OptimizedLineChartPainter(
        earnings: _weeklyEarnings,
        maxValue: maxValue,
        isSmallScreen: isSmallScreen,
      ),
      size: Size.infinite,
    );
  }
}

class OptimizedLineChartPainter extends CustomPainter {
  final List<DailyEarning> earnings;
  final double maxValue;
  final bool isSmallScreen;

  final List<String> _months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

  OptimizedLineChartPainter({
    required this.earnings,
    required this.maxValue,
    this.isSmallScreen = false,
  });

  @override
  void paint(Canvas canvas, Size size) {
    if (earnings.isEmpty) return;

    final double bottomPadding = isSmallScreen ? 30 : 40;
    final double rightPadding = isSmallScreen ? 40 : 60;
    final double leftPadding = isSmallScreen ? 30 : 40;
    final double height = size.height - bottomPadding;
    final double width = size.width - rightPadding - leftPadding;
    final double startX = leftPadding;

    final linePaint = Paint()
      ..color = Colors.red
      ..style = PaintingStyle.stroke
      ..strokeWidth = isSmallScreen ? 1.5 : 2.0
      ..strokeCap = StrokeCap.round;

    final glowPaint = Paint()
      ..color = Colors.red.withOpacity(0.2)
      ..style = PaintingStyle.stroke
      ..strokeWidth = isSmallScreen ? 3.0 : 4.0
      ..maskFilter = MaskFilter.blur(BlurStyle.normal, isSmallScreen ? 3 : 4);

    final gridPaint = Paint()
      ..color = Colors.grey.withOpacity(0.2)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 0.5;

    final List<Offset> points = _calculatePoints(startX, width, height);
    final path = _createPath(points);

    _drawVerticalGrid(canvas, points, height, gridPaint);

    canvas.drawPath(path, glowPaint);
    canvas.drawPath(path, linePaint);

    _drawPointsAndLabels(canvas, points, size, height);
  }

  List<Offset> _calculatePoints(double startX, double width, double height) {
    final points = <Offset>[];

    for (int i = 0; i < earnings.length; i++) {
      final x = startX + (i / (earnings.length - 1)) * width;
      final y = height - (earnings[i].netPay / maxValue * height);
      points.add(Offset(x, y));
    }

    return points;
  }

  Path _createPath(List<Offset> points) {
    final path = Path();

    if (points.isEmpty) return path;

    path.moveTo(points[0].dx, points[0].dy);

    for (int i = 1; i < points.length; i++) {
      final prevPoint = points[i-1];
      final currentPoint = points[i];

      final controlX = prevPoint.dx + (currentPoint.dx - prevPoint.dx) * 0.5;
      path.quadraticBezierTo(
          controlX, prevPoint.dy,
          currentPoint.dx, currentPoint.dy
      );
    }

    return path;
  }

  void _drawVerticalGrid(Canvas canvas, List<Offset> points, double height, Paint gridPaint) {
    final step = isSmallScreen && earnings.length > 5 ? 2 : 1;

    for (int i = 0; i < points.length; i += step) {
      canvas.drawLine(
        Offset(points[i].dx, 0),
        Offset(points[i].dx, height),
        gridPaint,
      );
    }
  }

  void _drawPointsAndLabels(Canvas canvas, List<Offset> points, Size size, double height) {
    final markerSize = isSmallScreen ? 4.0 : 6.0;
    final innerMarkerSize = isSmallScreen ? 3.0 : 4.0;

    for (int i = 0; i < points.length; i++) {
      final point = points[i];

      canvas.drawCircle(
        point,
        markerSize,
        Paint()..color = Colors.red,
      );

      canvas.drawCircle(
        point,
        innerMarkerSize,
        Paint()..color = Colors.white,
      );

      final dateText = _formatDate(earnings[i].date);
      _drawText(
        canvas,
        dateText,
        Offset(point.dx, height + 5),
        isSmallScreen ? 8 : 10,
        Colors.white70,
        TextAlign.center,
      );

      bool showValue = !isSmallScreen || (i % 2 == 0);

      if (showValue) {
        final valueText = '\$${earnings[i].netPay.toStringAsFixed(2)}';
        _drawText(
          canvas,
          valueText,
          Offset(point.dx, point.dy - (isSmallScreen ? 15 : 20)),
          isSmallScreen ? 10 : 12,
          Colors.white,
          TextAlign.center,
          FontWeight.bold,
        );
      }
    }
  }

  void _drawText(
      Canvas canvas,
      String text,
      Offset position,
      double fontSize,
      Color color,
      TextAlign align,
      [FontWeight weight = FontWeight.normal]
      ) {
    final textSpan = TextSpan(
      text: text,
      style: TextStyle(
        color: color,
        fontSize: fontSize,
        fontWeight: weight,
      ),
    );

    final textPainter = TextPainter(
      text: textSpan,
      textDirection: TextDirection.ltr,
      textAlign: align,
    );

    textPainter.layout();

    final offset = Offset(
      position.dx - (align == TextAlign.center ? textPainter.width / 2 : 0),
      position.dy,
    );

    textPainter.paint(canvas, offset);
  }

  String _formatDate(DateTime date) {
    if (isSmallScreen) {
      return '${date.day}/${date.month}';
    }
    return '${_months[date.month - 1]} ${date.day}';
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    if (oldDelegate is OptimizedLineChartPainter) {
      return oldDelegate.earnings != earnings ||
          oldDelegate.maxValue != maxValue ||
          oldDelegate.isSmallScreen != isSmallScreen;
    }
    return true;
  }
}