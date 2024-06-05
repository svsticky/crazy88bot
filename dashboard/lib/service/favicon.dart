import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:crazy88_dashboard/service/api.dart';
import 'package:http/http.dart' as http;

Future<Uint8List?> getFavicon() async {
  try {
    http.Response response = await http.get(Uri.parse("$url/favicon.ico"));
    if(response.statusCode == 200) {
      return response.bodyBytes;
    } else {
      debugPrint("Failed to get favicon ${response.statusCode}");
      return null;
    }
  } on Exception catch(e) {
    debugPrint(e.toString());
    return null;
  }

}