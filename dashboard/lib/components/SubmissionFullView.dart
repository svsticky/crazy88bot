import 'package:crazy88_dashboard/service/submissions.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class SubmissionFullView extends StatefulWidget {
  final List<Submission> items;
  final int index;
  final Function() next;
  final Function() previous;
  final Function(int grade) grade;

  const SubmissionFullView({super.key, required this.next, required this.items, required this.index, required this.previous, required this.grade});

  @override
  State<SubmissionFullView> createState() => _SubmissionFullViewState();
}

class _SubmissionFullViewState extends State<SubmissionFullView> {
  bool _denyLoading = false;
  bool _approveLoading = false;
  
  Submission _submission() => widget.items[widget.index];

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
                    onPressed: () => widget.next(),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Tooltip(
                  message: "W - Volgende",
                  child: IconButton(
                    icon: const Icon(Icons.arrow_forward),
                    onPressed: () => widget.next(),
                  ),
                ),
              )
            ],
          ),
        ),
        body: CallbackShortcuts(
          bindings: <ShortcutActivator, VoidCallback> {
            const SingleActivator(LogicalKeyboardKey.escape): () => Navigator.of(context).pop(),

            const SingleActivator(LogicalKeyboardKey.keyW): () => widget.next(),
            const SingleActivator(LogicalKeyboardKey.arrowRight): () => widget.next(),

            const SingleActivator(LogicalKeyboardKey.keyQ): () => widget.previous(),
            const SingleActivator(LogicalKeyboardKey.arrowLeft): () => widget.previous(),

            const SingleActivator(LogicalKeyboardKey.keyN): () => markDenied(),
            const SingleActivator(LogicalKeyboardKey.keyJ): () => markApproved(),
          },
          child: Focus(
            autofocus: true,
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 16.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                mainAxisSize: MainAxisSize.max,
                children: [
                  SingleChildScrollView(
                    child: Column(
                      textDirection: TextDirection.ltr,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        Text(
                          "Team ${_submission().teamId} - Opdracht ${_submission().id}",
                          style: const TextStyle(
                            fontSize: 26
                          )
                        ),
                        Text(
                          "“${_submission().assignment}”",
                          style: const TextStyle(
                            fontSize: 24
                          ),
                        ),
                        Card(
                          color: _submission().grade == null ? Colors.grey : (_submission().grade == 0 ? Colors.redAccent : Colors.green),
                          child: Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: _submission().grade == null
                                ? const Text("Nog niet gekeurd", style: TextStyle(color: Colors.white))
                                : _submission().grade == 0
                                  ? const Text("Afgekeurd", style: TextStyle(color: Colors.white))
                                  : const Text("Goedgekeurd", style: TextStyle(color: Colors.white)),
                          ),
                        ),
                        Image.network(
                          _submission().imageUrl(),
                          width: MediaQuery.of(context).size.width / 4 * 3,
                          height: MediaQuery.of(context).size.height / 4 * 3,
                        ),
                        Row(
                          children: [
                            Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: Tooltip(
                                message: "N - Afkeuren",
                                child: LoaderOr(
                                  loading: _denyLoading,
                                  child: FloatingActionButton(
                                    onPressed: markDenied,
                                    backgroundColor: Colors.redAccent,
                                    child: const Icon(Icons.close, color: Colors.white),
                                  ),
                                ),
                              ),
                            ),
                            Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: Tooltip(
                                message: "J - Goedkeuren",
                                child: LoaderOr(
                                  loading: _approveLoading, 
                                  child: FloatingActionButton(
                                    onPressed: markApproved,
                                    backgroundColor: Colors.green,
                                    child: const Icon(Icons.check, color: Colors.white),
                                  )
                                ),
                              ),
                            )
                          ],
                        )
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      )
    );
  }

  void markDenied() async {
    if(_denyLoading) return;

    setState(() => _denyLoading = true);
    bool ok = await mark(0);
    setState(() => _denyLoading = false);

    if(ok) {
      widget.grade(0);
      widget.next();
    }
  }

  void markApproved() async {
    if(_approveLoading) return;

    setState(() => _approveLoading = true);
    bool ok = await mark(10);
    setState(() => _approveLoading = false);

    if(ok) {
      widget.grade(10);
      widget.next();
    }
  }

  Future<bool> mark(int grade) async {
    bool ok = await _submission().setGrade(grade);
    if(context.mounted) {
      if(!ok) {
        ScaffoldMessenger.of(context).showMaterialBanner(MaterialBanner(
          content: const Text("Er is iets verkeerd gegaan", style: TextStyle(color: Colors.white)),
          actions: [
            IconButton(
              onPressed: () => ScaffoldMessenger.of(context).clearMaterialBanners(),
              icon: const Icon(Icons.close, color: Colors.white)
            )
          ],
          backgroundColor: Colors.redAccent,
        ));
      } else {
        _submission().grade = grade;
      }
    }

    return ok;
  }
}

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