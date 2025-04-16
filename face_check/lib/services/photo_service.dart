import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';
import 'package:image_picker/image_picker.dart';

class PhotoService {
  static Future<String?> encodeImageToBase64(File imageFile) async {
    try {
      final bytes = await imageFile.readAsBytes();
      final base64String = base64Encode(bytes);
      return base64String;
    } catch (e) {
      print('Error encoding image to base64: $e');
      return null;
    }
  }

  static Future<String?> encodeXFileToBase64(XFile imageFile) async {
    try {
      final bytes = await imageFile.readAsBytes();
      final base64String = base64Encode(bytes);
      return base64String;
    } catch (e) {
      print('Error encoding XFile to base64: $e');
      return null;
    }
  }

  static Future<File?> decodeBase64ToFile(String base64String, String filePath) async {
    try {
      final bytes = base64Decode(base64String);
      final file = File(filePath);
      await file.writeAsBytes(bytes);
      return file;
    } catch (e) {
      print('Error decoding base64 to file: $e');
      return null;
    }
  }

  static Uint8List? decodeBase64ToBytes(String base64String) {
    try {
      return base64Decode(base64String);
    } catch (e) {
      print('Error decoding base64 to bytes: $e');
      return null;
    }
  }

  static bool isValidBase64Image(String base64String) {
    try {
      base64Decode(base64String);
      return base64String.contains('data:image') ||
          RegExp(r'^[A-Za-z0-9+/]*={0,2}$').hasMatch(base64String);
    } catch (e) {
      return false;
    }
  }

  static Future<String?> compressAndEncodeImage(File imageFile) async {
    try {
      return await encodeImageToBase64(imageFile);
    } catch (e) {
      print('Error compressing and encoding image: $e');
      return null;
    }
  }
}