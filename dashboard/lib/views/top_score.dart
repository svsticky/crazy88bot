import 'package:crazy88_dashboard/components/card_footer.dart';
import 'package:crazy88_dashboard/components/load_or.dart';
import 'package:crazy88_dashboard/util/pair.dart';
import 'package:crazy88_dashboard/service/team.dart';
import 'package:flutter/material.dart';

class TopScoreView extends StatefulWidget {
  const TopScoreView({super.key});

  @override
  State<StatefulWidget> createState() => _TopScoreViewState();
}

class _TopScoreViewState extends State<TopScoreView> {
  bool _loading = false;
  List<Pair<Team, int>> _scores = [];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => _loadData());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Top Score"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Card(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: ListView(
                    children: [
                      const Center(
                        child: Text("Top scores", style: TextStyle(fontSize: 30))
                      ),
                      LoaderOr(
                        loading: _loading,
                        child: Column(
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                _getPodiumPlace(1),
                                _getPodiumPlace(0),
                                _getPodiumPlace(2),
                              ],
                            ),
                            const Padding(
                              padding: EdgeInsets.only(top: 16.0),
                              child: Text("Other teams:", style: TextStyle(fontSize: 25)),
                            ),
                            Column(
                              children: _getNonPodiumPlaces(),
                            ),
                          ],
                        ),
                      ),
                    ]
                  ),
                ),
                const StickyCardFooter()
              ],
            ),
          ),
        ),
      ),
    );
  }

  List<Widget> _getNonPodiumPlaces() {
    if(_scores.length <= 3) {
      return [
        const Text("Nothing here...")
      ];
    }
    List<int> indices = Iterable<int>.generate(_scores.length - 3).toList();

    return indices.map((idx) {
      Pair<Team, int> score = _scores[idx - 3];
      return SizedBox(
        width: 300,
        child: Card(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Align(
                  alignment: Alignment.topLeft,
                  child: Text("${idx + 1}"),
                ),
                Text("Team ${score.a.id}", textScaler: const TextScaler.linear(1.2)),
                Text("Score: ${score.b}")
              ],
            ),
          )
        ),
      );
    }).toList();
  }

  Widget _getPodiumPlace(int position) {
    double size = position == 0 ? 200 : 150;

    if(_scores.length <= position) return _getEmptyPodium(size);

    Pair<Team, int> score = _scores[position];
    return SizedBox(
      height: size,
      width: size,
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Icon(Icons.star, color: _getStarColor(position), size: size / 2),
              Text(
                "Team ${score.a.id}",
                textScaler: const TextScaler.linear(1.4),
              ),
              Text(
                "Score: ${score.b}"
              )
            ],
          ),
        ),
      ),
    );
  }

  Color _getStarColor(int position) {
    switch(position) {
      case 0:
        return Colors.yellow;
      case 1:
        return Colors.blueGrey;
      case 2:
        return Colors.orangeAccent;
      default:
        throw ArgumentError("Argument is not a podium position");
    }
  }

  Widget _getEmptyPodium(double size) {
    return SizedBox(
      height: size,
      width: size,
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            children: [
              Icon(Icons.star_border, size: size / 3 * 2),
              const Text("Empty")
            ],
          ),
        ),
      ),
    );
  }

  void _loadData() async {
    setState(() {
      _loading = true;
    });

    List<Team> teams = await _loadTeams();
    List<Pair<Team, int>?> mScores = await Future.wait(teams.map((team) => _loadScore(team)));
    List<Pair<Team, int>> scores = mScores
        .where((element) => element != null)
        .map((e) => e!)
        .toList();

    scores.sort((a, b) => b.b - a.b);

    setState(() {
      _scores = scores;
      _loading = false;
    });
  }

  Future<List<Team>> _loadTeams() async {
    List<Team>? mTeams = await listTeams();
    if(!context.mounted) return [];
    if(mTeams == null) {
      _showErrorBanner();
      return [];
    }

    return mTeams;
  }

  Future<Pair<Team, int>?> _loadScore(Team team) async {
    int? totalScore = await team.getTotalScore();
    if(!context.mounted) return null;
    if(totalScore == null) {
      _showErrorBanner();
      return null;
    }

    return Pair(team, totalScore);
  }

  void _showErrorBanner() {
    ScaffoldMessenger.of(context).showMaterialBanner(MaterialBanner(
        content: const Text("Er is iets fout gegaan"),
        backgroundColor: Colors.redAccent,
        actions: [
          IconButton(onPressed: () => ScaffoldMessenger.of(context).clearMaterialBanners(), icon: const Icon(Icons.close))
        ]
    ));
  }
}