import 'package:flutter/material.dart';

class LoaderOr extends StatelessWidget {
  final bool loading;
  final Widget child;
  final double size;

  const LoaderOr({super.key, required this.loading, required this.child, this.size = 40.0});

  @override
  Widget build(BuildContext context) {
    if(loading) {
      return SizedBox(
        width: size,
        height: size,
        child: const CircularProgressIndicator(),
      );
    } else {
      return child;
    }
  }
}