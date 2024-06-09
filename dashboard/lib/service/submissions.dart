import 'dart:convert';
import 'dart:typed_data';

import 'package:crazy88_dashboard/service/api.dart';
import 'package:crazy88_dashboard/service/team.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class Submission {
  final int teamId;
  final int id;
  final String assignment;
  int? grade;

  Submission({required this.teamId, required this.id, required this.assignment, this.grade});

  String imageUrl() => "$url/submissions?teamId=$teamId&assignmentId=$id";

  Future<bool> setGrade(int grade) async {
    try {
      Map<String, dynamic> body = {
        'assignmentId': id,
        'teamId': teamId,
        'grade': grade
      };

      http.Response response = await http.post(Uri.parse("$url/submissions/grade"), body: jsonEncode(body), headers: {
        'Content-Type': 'application/json'
      });

      bool ok = response.statusCode == 200;
      if(ok) {
        return ok;
      } else {
        debugPrint("Failed to set grade (${response.statusCode}): ${response.body}");
        return false;
      }
    } on Exception catch(e) {
      debugPrint(e.toString());
      return false;
    }
  }
}

Future<List<Submission>?> listSubmissions(int teamId) async {
  try {
    http.Response response = await http.get(Uri.parse("$url/submissions/list?teamId=$teamId"));
    if(response.statusCode == 200) {
      Map<String, dynamic> payload = jsonDecode(response.body);
      List<dynamic> submitted = payload['submitted'];
      return submitted.map((map) => Submission(
          teamId: teamId,
          id: map['id'],
          assignment: map['assignment'],
          grade: map['grade'],
      )).toList();
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