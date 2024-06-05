import 'package:crazy88_dashboard/components/card_footer.dart';
import 'package:crazy88_dashboard/service/favicon.dart';
import 'package:crazy88_dashboard/views/submissions.dart';
import 'package:flutter/material.dart';

class DashboardView extends StatelessWidget {
  const DashboardView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Card(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Text("Crazy88 Bot", style: TextStyle(fontSize: 30)),
                    Padding(
                      padding: const EdgeInsets.only(top: 15.0),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          DashboardButton(
                            icon: Icons.image,
                            text: "Inzendingen",
                            onPressed: () => Navigator.of(context).push(MaterialPageRoute(builder: (_) => const SubmissionsView())),
                          )
                        ],
                      ),
                    ),
                  ],
                ),
                const StickyCardFooter(),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class DashboardButton extends StatelessWidget {
  final IconData icon;
  final String text;
  final Function() onPressed;

  const DashboardButton({super.key, required this.icon, required this.text, required this.onPressed});

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      style: ButtonStyle(
          elevation: MaterialStateProperty.all(8.0),
          shape: MaterialStateProperty.all(RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10.0)
          ))
      ),
      onPressed: onPressed,
      child: Row(
        children: [
          Icon(icon),
          Text(text)
        ],
      ),
    );
  }

}