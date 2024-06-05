import 'dart:io';
import 'package:flutter/material.dart';
import 'package:crazy88_dashboard/service/api.dart';
import 'package:http/http.dart' as http;


Future<String?> getFavicon() async {
  http.Request request = http.Request('GET', Uri.parse("$url/favicon.ico"));
  request.followRedirects = false;

  try {
    http.Response response = await http.Response.fromStream(await request.send());
    if(response.isRedirect) {
      return response.headers['location'];
    } else {
      return null;
    }
  } on Exception catch(e) {
    debugPrint(e.toString());
    return null;
  }

}