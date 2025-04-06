import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';
import 'package:image_picker/image_picker.dart';

class PhotoService {
  /// Конвертирует файл изображения в строку base64
  static Future<String?> encodeImageToBase64(File imageFile) async {
    try {
      // Читаем файл как список байтов
      final bytes = await imageFile.readAsBytes();
      // Конвертируем bytes в base64 строку
      final base64String = base64Encode(bytes);
      return base64String;
    } catch (e) {
      print('Error encoding image to base64: $e');
      return null;
    }
  }

  /// Конвертирует XFile (из image_picker) в base64
  static Future<String?> encodeXFileToBase64(XFile imageFile) async {
    try {
      // Читаем файл как список байтов
      final bytes = await imageFile.readAsBytes();
      // Конвертируем bytes в base64 строку
      final base64String = base64Encode(bytes);
      return base64String;
    } catch (e) {
      print('Error encoding XFile to base64: $e');
      return null;
    }
  }

  /// Декодирует строку base64 в файл изображения
  static Future<File?> decodeBase64ToFile(String base64String, String filePath) async {
    try {
      // Декодируем base64 в bytes
      final bytes = base64Decode(base64String);
      // Создаем новый файл
      final file = File(filePath);
      // Записываем bytes в файл
      await file.writeAsBytes(bytes);
      return file;
    } catch (e) {
      print('Error decoding base64 to file: $e');
      return null;
    }
  }

  /// Декодирует строку base64 в Uint8List (может быть полезно для отображения изображения)
  static Uint8List? decodeBase64ToBytes(String base64String) {
    try {
      return base64Decode(base64String);
    } catch (e) {
      print('Error decoding base64 to bytes: $e');
      return null;
    }
  }

  /// Проверяет, является ли строка валидным base64 изображением
  static bool isValidBase64Image(String base64String) {
    try {
      // Пытаемся декодировать строку
      base64Decode(base64String);
      // Проверяем, начинается ли строка с правильного префикса изображения
      return base64String.contains('data:image') ||
          RegExp(r'^[A-Za-z0-9+/]*={0,2}$').hasMatch(base64String);
    } catch (e) {
      return false;
    }
  }

  /// Сжимает изображение перед конвертацией в base64 (если нужно)
  static Future<String?> compressAndEncodeImage(File imageFile) async {
    try {
      // Здесь можно добавить логику сжатия изображения
      // Например, использовать package:image для изменения размера
      // Пока просто возвращаем обычное кодирование
      return await encodeImageToBase64(imageFile);
    } catch (e) {
      print('Error compressing and encoding image: $e');
      return null;
    }
  }
}