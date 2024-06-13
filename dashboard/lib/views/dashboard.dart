import 'package:crazy88_dashboard/components/card_footer.dart';
import 'package:crazy88_dashboard/views/submissions.dart';
import 'package:crazy88_dashboard/views/top_score.dart';
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
                          ),
                          DashboardButton(
                            icon: Icons.star,
                            text: "Topscores",
                            onPressed: () => Navigator.of(context).push(MaterialPageRoute(builder: (_) => const TopScoreView())),
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
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: ElevatedButton(
        style: ButtonStyle(
            elevation: MaterialStateProperty.all(8.0),
            shape: MaterialStateProperty.all(RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10.0)
            ))
        ),
        onPressed: onPressed,
        child: Row(
          children: [
            Icon(icon, color: Colors.black),
            Text(text, style: const TextStyle(color: Colors.black))
          ],
        ),
      ),
    );
  }

}