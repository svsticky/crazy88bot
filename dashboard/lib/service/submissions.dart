import 'dart:convert';
import 'dart:typed_data';

import 'package:crazy88_dashboard/service/api.dart';
import 'package:crazy88_dashboard/service/team.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class Submission {
  final int teamId;
  final int id;

  Submission({required this.teamId, required this.id});

  String imageUrl() => "$url/submissions?teamId=$teamId&assignmentId=$id";
}

Future<List<Submission>?> listSubmissions(int teamId) async {
  try {
    http.Response response = await http.get(Uri.parse("$url/submissions/list?teamId=$teamId"));
    if(response.statusCode == 200) {
      Map<String, dynamic> payload = jsonDecode(response.body);
      List<dynamic> submitted = payload['submitted'];
      return submitted.map((e) => Submission(teamId: teamId, id: e)).toList();
    } else {
      debugPrint("Failed to list submissions: ${response.statusCode}");
    }
  } on Exception catch(e) {
    debugPrint(e.toString());
  }

  return null;
}

Future<Uint8List?> getSubmissionImage(Submission submission) async {
  try {
    http.Response response = await http.get(Uri.parse("$url/submissions?teamId=${submission.teamId}&assignmentId=${submission.id}"));
    if(response.statusCode == 200) {
      return response.bodyBytes;
    } else {
      debugPrint("Failed to get submission image: ${response.statusCode}");
    }
  } on Exception catch(e) {
    debugPrint(e.toString());
  }

  return null;
}

Future<List<Submission>> listAllSubmissions() async {
  List<Team>? teams = await listTeams();
  if(teams == null) {
    return [];
  }

  List<Submission> result = List.empty(growable: true);

  for (Team value in teams) {
    List<Submission>? submissions = await listSubmissions(value.id);
    if(submissions == null) {
      continue;
    }

    result.addAll(submissions);
  }

  return result;
}