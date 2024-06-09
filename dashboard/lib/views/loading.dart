import 'dart:async';
import 'dart:typed_data';

import 'package:crazy88_dashboard/service/favicon.dart';
import 'package:flutter/material.dart';

class LoadingView extends StatefulWidget {
  final Function() onComplete;

  const LoadingView({super.key, required this.onComplete});

  @override
  State<LoadingView> createState() => _LoadingViewState();
}

class _LoadingViewState extends State<LoadingView> {
  Uint8List? _favicon;
  String? _error;
  bool _loading = true;

  static const double _iconSize = 80;

  @override
  void initState() {
   super.initState();

   WidgetsBinding.instance.addPostFrameCallback((timeStamp) => _loadIcon());
  }

  void _loadIcon() async {
    Uint8List? s = await getFavicon();
    if(context.mounted) {
      if(s != null) {
        setState(() {
          _favicon = s;
          _loading = false;
        });
      } else {
        setState(() {
          _error = "De server is niet bereikbaar";
          _loading = false;
        });

        ScaffoldMessenger.of(context).showMaterialBanner(
            MaterialBanner(
              content: Text(_error!, style: const TextStyle(color: Colors.white)),
              backgroundColor: Colors.redAccent,
              actions: [
                IconButton(onPressed: () {
                  ScaffoldMessenger
                      .of(context)
                      .clearMaterialBanners();
                  Timer(const Duration(milliseconds: 500), () {
                    setState(() {
                      _loading = true;
                      _error = null;
                      _favicon = null;
                    });
                    _loadIcon();
                  });
                }, icon: const Icon(Icons.refresh, color: Colors.white))
              ],
            )
        );
      }
    }
  }
  
  @override
  Widget build(BuildContext context) {
    Widget content;
    if(_loading) {
      content = _getLoader();
    } else if (_favicon != null) {
      content = _getFavicon();
      Timer(const Duration(seconds: 2), () {
        if(context.mounted) {
          widget.onComplete();
        }
      });
    } else {
      content = _getErrorIcon();
    }

    return Scaffold(
      body: Center(
        child: content,
      ),
    );
  }

  Widget _getFavicon() {
    return SizedBox(
      width: _iconSize,
      height: _iconSize,
      child: Image.memory(_favicon!),
    );
  }

  Widget _getErrorIcon() {
    return const SizedBox(
      width: _iconSize,
      height: _iconSize,
      child: Icon(Icons.warning_amber, color: Colors.redAccent, size: 50),
    );
  }

  Widget _getLoader() {
    return const SizedBox(
      width: _iconSize,
      height: _iconSize,
      child: CircularProgressIndicator(),
    );
  }
}
