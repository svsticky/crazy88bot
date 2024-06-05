import 'package:crazy88_dashboard/views/dashboard.dart';
import 'package:crazy88_dashboard/views/loading.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        appBarTheme: const AppBarTheme(
          backgroundColor: Colors.blue,
          titleTextStyle: TextStyle(
            color: Colors.white,
          ),
          iconTheme: IconThemeData(
            color: Colors.white,
          )
        )
      ),
      home: const AppView(),
    );
  }
}

class AppView extends StatelessWidget {
  const AppView({super.key});

  @override
  Widget build(BuildContext context) {
    return LoadingView(
      onComplete: () => Navigator.of(context).pushReplacement(MaterialPageRoute(builder: (_) => const DashboardView())),
    );
  }

}