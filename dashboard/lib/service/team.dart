import 'dart:convert';

import 'package:crazy88_dashboard/service/api.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class Team {
  final int id;

  Team({required this.id});

  Future<int?> getTotalScore() async {
    try {
      http.Response response = await http.get(Uri.parse("$url/teams/score?teamId=$id"));
      if(response.statusCode == 200) {
        Map<dynamic, dynamic> payload = jsonDecode(response.body);
        return payload['totalScore'];
      } else {
        debugPrint("Failed to load total score: ${response.statusCode}");
      }
    } on Exception catch (e) {
      debugPrint(e.toString());
    }

    return null;
  }
}

Future<List<Team>?> listTeams() async {
  try {
    http.Response response = await http.get(Uri.parse("$url/teams/list"));
    if(response.statusCode == 200) {
      Map<String, dynamic> payload = jsonDecode(response.body);
      List<dynamic> teams = payload['teams'];
      return teams.map((team) => Team(id: team['id'])).toList();
    } else {
      debugPrint("Failed to load teams: ${response.statusCode}");
    }
  } on Exception catch(e) {
    debugPrint(e.toString());
  }

  return null;
}