import 'package:crazy88_dashboard/components/submission_full_view.dart';
import 'package:crazy88_dashboard/components/card_footer.dart';
import 'package:crazy88_dashboard/service/submissions.dart';
import 'package:flutter/material.dart';

class SubmissionsView extends StatefulWidget {
  const SubmissionsView({super.key});

  @override
  State<SubmissionsView> createState() => _SubmissionsViewState();
}

class _SubmissionsViewState extends State<SubmissionsView> {
  bool _loading = true;
  List<Submission>? _submissions;
  int? _dialogIndex;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((timeStamp) => _loadSubmissions());
  }

  void _loadSubmissions() async {
    setState(() {
      _loading = true;
    });

    List<Submission> submissions = await listAllSubmissions();
    setState(() {
      _submissions = submissions;
      _loading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Row(
          children: [
            SizedBox(
              width: 30,
              height: 30,
              child: Icon(Icons.image)
            ),
            Text("Inzendingen")
          ],
        ),
        actions: [
          IconButton(
            onPressed: _loadSubmissions,
            icon: const Icon(Icons.refresh, color: Colors.white)
          )
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Card(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _loading ? _getLoader() : _getGrid(),
                const StickyCardFooter(),
              ],
            ),
          ),
        ),
      )
    );
  }

  Widget _getLoader() {
    return const SizedBox(
      width: 80,
      height: 80,
      child: CircularProgressIndicator(),
    );
  }

  Widget _getGrid() {
    return Expanded(
      child: GridView.builder(
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 5,
          crossAxisSpacing: 10,
        ),
        shrinkWrap: true,
        physics: const ScrollPhysics(),
        scrollDirection: Axis.vertical,
        itemCount: _submissions!.length,
        itemBuilder: (context, index) {
          Submission submission = _submissions![index];
          return Card(
            elevation: 4,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12.0),
              side: BorderSide(
                color: _getBorderColor(_submissions![index]),
                width: 2.0
              ),
            ),
            child: InkWell(
              onTap: () => {
                showDialog(context: context, builder: (_) {
                  _dialogIndex = index;

                  return StatefulBuilder(
                    builder: (context, setState) => SubmissionFullView(
                      items: _submissions!,
                      index: _dialogIndex ?? index,
                      next: () {
                        int currIndex = _dialogIndex ?? index;
                        int nextIdx = currIndex + 1 >= _submissions!.length ? 0 : currIndex + 1;
                        setState(() {
                          _dialogIndex = nextIdx;
                        });
                      },
                      previous: () {
                        int currIndex = _dialogIndex ?? index;
                        int nextIdx = currIndex - 1 < 0 ? _submissions!.length - 1 : 0;
                        setState(() {
                          _dialogIndex = nextIdx;
                        });
                      },
                      grade: (grade) {
                        setState(() => _submissions![index].grade = grade);
                      },
                    ),
                  );
                })
              },
              child: Padding(
                padding: const EdgeInsets.all(4.0),
                child: Image.network(submission.imageUrl()),
              ),
            ),
          );
        },
      ),
    );
  }

  Color _getBorderColor(Submission s) {
    return s.grade == null
        ? Colors.grey
        : (s.grade == 0
          ? Colors.redAccent
          : Colors.green);
  }
}