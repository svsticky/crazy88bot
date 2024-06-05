import 'dart:async';

import 'package:crazy88_dashboard/service/api.dart';
import 'package:crazy88_dashboard/service/favicon.dart';
import 'package:flutter/material.dart';

class LoadingView extends StatefulWidget {
  const LoadingView({super.key});

  @override
  State<LoadingView> createState() => _LoadingViewState();
}

class _LoadingViewState extends State<LoadingView> {
  String? _faviconUrl;
  String? _error;
  bool _loading = true;

  @override
  void initState() {
   super.initState();

   Timer(const Duration(seconds: 3), () => _loadIcon());
  }

  void _loadIcon() async {
    String? s = await getFavicon();
    if(context.mounted) {
      if(s != null) {
        setState(() {
          _faviconUrl = s;
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
                  Timer(const Duration(seconds: 2), () {
                    setState(() {
                      _loading = true;
                      _error = null;
                      _faviconUrl = null;
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
    } else if (_faviconUrl != null) {
      content = _getFavicon();
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
      width: 50,
      height: 50,
      child: Image.network(_faviconUrl!),
    );
  }

  Widget _getErrorIcon() {
    return const SizedBox(
      width: 50,
      height: 50,
      child: Icon(Icons.warning_amber, color: Colors.redAccent, size: 50),
    );
  }

  Widget _getLoader() {
    return const SizedBox(
      width: 50,
      height: 50,
      child: CircularProgressIndicator(),
    );
  }
}
