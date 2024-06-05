import 'package:crazy88_dashboard/service/favicon.dart';
import 'package:flutter/material.dart';

class StickyCardFooter extends StatelessWidget {
  const StickyCardFooter({super.key});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        SizedBox(
          width: 30,
          height: 30,
          child: FutureBuilder(future: getFavicon(), builder: (context, data) {
            if(data.hasData) {
              return Image.memory(data.data!);
            } else {
              return const CircularProgressIndicator();
            }
          }),
        ),
        const Text("Overenthusiastic Sticky"),
      ],
    );
  }
}