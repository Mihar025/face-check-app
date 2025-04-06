import 'dart:typed_data';
import 'package:open_filex/open_filex.dart';
import 'package:path_provider/path_provider.dart';
import 'package:pdf/pdf.dart';
import 'package:pdf/widgets.dart' as pw;
import 'package:intl/intl.dart';
import 'package:flutter/services.dart';
import 'package:printing/printing.dart';
import 'dart:io';

import '../models/finance_info_response.dart';



class FinancePdfService {
  static Future<void> generateFinanceReport(FinanceInfoResponse financeInfo) async {
    final pdf = pw.Document();

    // Загружаем шрифты
    final font = await PdfGoogleFonts.robotoRegular();
    final boldFont = await PdfGoogleFonts.robotoBold();

    // Загружаем логотип
    final ByteData logoBytes = await rootBundle.load('assets/images/logo.jpg');
    final Uint8List logoData = logoBytes.buffer.asUint8List();
    final pw.MemoryImage logoImage = pw.MemoryImage(logoData);

    // Стили для документа
    final titleStyle = pw.TextStyle(
      fontSize: 24,
      font: boldFont,
    );
    final headerStyle = pw.TextStyle(
        fontSize: 14,
        font: boldFont,
        color: PdfColors.blue900
    );
    final subHeaderStyle = pw.TextStyle(
      fontSize: 12,
      font: font,
      color: PdfColors.grey700,
    );
    final contentStyle = pw.TextStyle(
        fontSize: 12,
        font: font
    );

    // Форматирование дат
    final dateFormatter = DateFormat('MM/dd/yyyy');
    final weekDayFormatter = DateFormat('EEE');

    pdf.addPage(
      pw.Page(
        pageFormat: PdfPageFormat.a4,
        margin: const pw.EdgeInsets.all(32),
        build: (context) {
          return pw.Column(
            crossAxisAlignment: pw.CrossAxisAlignment.start,
            children: [
              // Шапка с логотипом
              pw.Row(
                mainAxisAlignment: pw.MainAxisAlignment.spaceBetween,
                children: [
                  pw.Image(logoImage, width: 120),
                  pw.Column(
                    crossAxisAlignment: pw.CrossAxisAlignment.end,
                    children: [
                      pw.Text('Financial Report', style: titleStyle),
                      pw.SizedBox(height: 4),
                      pw.Text(
                        '${dateFormatter.format(financeInfo.periodStart)} - ${dateFormatter.format(financeInfo.periodEnd)}',
                        style: subHeaderStyle,
                      ),
                    ],
                  ),
                ],
              ),
              pw.SizedBox(height: 32),

              // Сводная информация
              pw.Container(
                padding: const pw.EdgeInsets.all(16),
                decoration: pw.BoxDecoration(
                  color: PdfColors.blue50,
                  borderRadius: pw.BorderRadius.circular(8),
                ),
                child: pw.Row(
                  mainAxisAlignment: pw.MainAxisAlignment.spaceAround,
                  children: [
                    _buildSummaryItem(
                      'Total Hours',
                      financeInfo.totalHoursWorked.toStringAsFixed(1),
                      headerStyle,
                      contentStyle,
                    ),
                    _buildSummaryItem(
                      'Total Gross',
                      '\$${financeInfo.totalGrossPay.toStringAsFixed(2)}',
                      headerStyle,
                      contentStyle,
                    ),
                    _buildSummaryItem(
                      'Total Net',
                      '\$${financeInfo.totalNetPay.toStringAsFixed(2)}',
                      headerStyle,
                      contentStyle,
                    ),
                    _buildSummaryItem(
                      'Total Taxes',
                      '\$${(financeInfo.totalGrossPay - financeInfo.totalNetPay).toStringAsFixed(2)}',
                      headerStyle,
                      contentStyle,
                    ),
                  ],
                ),
              ),
              pw.SizedBox(height: 32),

              // Детальная таблица
              pw.Table(
                border: pw.TableBorder.all(color: PdfColors.grey300),
                columnWidths: {
                  0: const pw.FlexColumnWidth(2),
                  1: const pw.FlexColumnWidth(1),
                  2: const pw.FlexColumnWidth(1.5),
                  3: const pw.FlexColumnWidth(1.5),
                },
                children: [
                  // Заголовок таблицы
                  pw.TableRow(
                    decoration: pw.BoxDecoration(color: PdfColors.grey100),
                    children: [
                      pw.Padding(
                        padding: const pw.EdgeInsets.all(8),
                        child: pw.Text('Day', style: headerStyle),
                      ),
                      pw.Padding(
                        padding: const pw.EdgeInsets.all(8),
                        child: pw.Text('Hours', style: headerStyle),
                      ),
                      pw.Padding(
                        padding: const pw.EdgeInsets.all(8),
                        child: pw.Text('Gross', style: headerStyle),
                      ),
                      pw.Padding(
                        padding: const pw.EdgeInsets.all(8),
                        child: pw.Text('Net', style: headerStyle),
                      ),
                    ],
                  ),
                  // Строки с данными
                  ...financeInfo.dailyInfo.map((daily) => pw.TableRow(
                    children: [
                      pw.Padding(
                        padding: const pw.EdgeInsets.all(8),
                        child: pw.Column(
                          crossAxisAlignment: pw.CrossAxisAlignment.start,
                          children: [
                            pw.Text(
                              weekDayFormatter.format(daily.date).toUpperCase(),
                              style: headerStyle,
                            ),
                            pw.Text(
                              dateFormatter.format(daily.date),
                              style: contentStyle,
                            ),
                          ],
                        ),
                      ),
                      pw.Padding(
                        padding: const pw.EdgeInsets.all(8),
                        child: pw.Text(
                          daily.hoursWorked.toStringAsFixed(1),
                          style: contentStyle,
                        ),
                      ),
                      pw.Padding(
                        padding: const pw.EdgeInsets.all(8),
                        child: pw.Text(
                          '\$${daily.grossPay.toStringAsFixed(2)}',
                          style: contentStyle,
                        ),
                      ),
                      pw.Padding(
                        padding: const pw.EdgeInsets.all(8),
                        child: pw.Text(
                          '\$${daily.grossPay.toStringAsFixed(2)}',
                          style: contentStyle,
                        ),
                      ),
                    ],
                  )),
                ],
              ),
            ],
          );
        },
      ),
    );

    final output = await getApplicationDocumentsDirectory();
    final formattedDate = dateFormatter.format(financeInfo.periodStart).replaceAll('/', '_');
    final file = File('${output.path}/finance_report_$formattedDate.pdf');
    await file.writeAsBytes(await pdf.save());
    await OpenFilex.open(file.path);
    print('PDF сохранен по пути: ${file.path}');

  }

  static pw.Widget _buildSummaryItem(
      String label,
      String value,
      pw.TextStyle headerStyle,
      pw.TextStyle contentStyle,
      ) {
    return pw.Column(
      crossAxisAlignment: pw.CrossAxisAlignment.start,
      children: [
        pw.Text(label, style: headerStyle),
        pw.SizedBox(height: 4),
        pw.Text(value, style: contentStyle),
      ],
    );
  }
}