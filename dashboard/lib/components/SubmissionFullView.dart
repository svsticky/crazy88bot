import 'package:crazy88_dashboard/service/submissions.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class SubmissionFullView extends StatelessWidget {
  final List<Submission> items;
  final int index;
  final Function() next;
  final Function() previous;

  const SubmissionFullView({super.key, required this.next, required this.items, required this.index, required this.previous});

  @override
  Widget build(BuildContext context) {
    return Dialog.fullscreen(
      child: Scaffold(
        appBar: AppBar(
          centerTitle: true,
          title: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: [
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Tooltip(
                  message: "Q - Vorige",
                  child: IconButton(
                    icon: const Icon(Icons.arrow_back),
                    onPressed: () => next(),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Tooltip(
                  message: "W - Volgende",
                  child: IconButton(
                    icon: const Icon(Icons.arrow_forward),
                    onPressed: () => next(),
                  ),
                ),
              )
            ],
          ),
        ),
        body: CallbackShortcuts(
          bindings: <ShortcutActivator, VoidCallback> {
            const SingleActivator(LogicalKeyboardKey.escape): () => Navigator.of(context).pop(),

            const SingleActivator(LogicalKeyboardKey.keyW): () => next(),
            const SingleActivator(LogicalKeyboardKey.arrowRight): () => next(),

            const SingleActivator(LogicalKeyboardKey.keyQ): () => previous(),
            const SingleActivator(LogicalKeyboardKey.arrowLeft): () => previous()
          },
          child: Focus(
            autofocus: true,
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text(
                    "TODO"
                  ),
                  Image.network(
                    items[index].imageUrl(),
                    width: MediaQuery.of(context).size.width / 4 * 3,
                    height: MediaQuery.of(context).size.height / 4 * 3,
                  ),
                ],
              ),
            ),
          ),
        ),
      )
    );
  }
}