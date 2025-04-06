import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../../../../providers/localization_provider.dart';
import '../../../../../api_client/model/work_site_response.dart';

class WorkSiteDialog extends StatefulWidget {
  final List<WorkSiteResponse> workSites;
  final Function(WorkSiteResponse) onSelect;
  final bool isLoading;
  final VoidCallback onRefresh;

  const WorkSiteDialog({
    required this.workSites,
    required this.onSelect,
    required this.isLoading,
    required this.onRefresh,
    super.key,
  });

  @override
  State<WorkSiteDialog> createState() => _WorkSiteDialogState();
}

class _WorkSiteDialogState extends State<WorkSiteDialog> {
  String searchQuery = '';
  bool isSelecting = false;

  @override
  Widget build(BuildContext context) {
    final l10n = context.read<LocalizationProvider>().localizations;
    final screenSize = MediaQuery.of(context).size;
    final sizeFactor = screenSize.width < 360 ? 0.9 : 1.0;
    final filteredSites = widget.workSites
        .where((site) => site.workSiteName?.toLowerCase().contains(searchQuery.toLowerCase()) ?? false)
        .toList();

    return PopScope(
      canPop: !isSelecting,
      child: Dialog(
        backgroundColor: Colors.transparent,
        child: Container(
          constraints: BoxConstraints(maxHeight: screenSize.height * 0.8),
          decoration: BoxDecoration(
            color: const Color(0xFF1E1E1E),
            borderRadius: BorderRadius.circular(28 * sizeFactor),
            border: Border.all(color: const Color(0xFF303030), width: 1.5 * sizeFactor),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.4),
                blurRadius: 24 * sizeFactor,
                spreadRadius: 2 * sizeFactor,
              ),
            ],
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildHeader(l10n, sizeFactor),
              _buildSearchField(l10n, sizeFactor),
              Expanded(child: _buildWorkSitesList(filteredSites, l10n, sizeFactor)),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHeader(localizations, double sizeFactor) {
    return Container(
      padding: EdgeInsets.all(24 * sizeFactor),
      decoration: const BoxDecoration(
        border: Border(bottom: BorderSide(color: Color(0xFF303030), width: 1)),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              _buildIcon(Icons.business_outlined, sizeFactor),
              SizedBox(width: 8 * sizeFactor),
              Text(
                localizations.get('selectWorkSite'),
                style: _textStyle(17 * sizeFactor, FontWeight.w600, Colors.grey[200]),
                overflow: TextOverflow.ellipsis,
                maxLines: 1,
              ),
            ],
          ),
          if (!isSelecting)
            IconButton(
              icon: Icon(Icons.close, color: Colors.grey[400], size: 22 * sizeFactor),
              onPressed: () => Navigator.pop(context),
            ),
        ],
      ),
    );
  }

  Widget _buildSearchField(localizations, double sizeFactor) {
    return Container(
      margin: EdgeInsets.symmetric(horizontal: 28 * sizeFactor, vertical: 16 * sizeFactor),
      decoration: BoxDecoration(
        color: const Color(0xFF2A2A2A),
        borderRadius: BorderRadius.circular(16 * sizeFactor),
        border: Border.all(color: const Color(0xFF363636)),
      ),
      child: TextField(
        enabled: !isSelecting,
        style: _textStyle(16 * sizeFactor, FontWeight.w400, Colors.grey[300]),
        decoration: InputDecoration(
          hintText: localizations.get('searchWorkSites'),
          hintStyle: _textStyle(16 * sizeFactor, FontWeight.w400, Colors.grey[600]),
          border: InputBorder.none,
          prefixIcon: Icon(Icons.search, color: Colors.grey[600], size: 22 * sizeFactor),
          contentPadding: EdgeInsets.all(16 * sizeFactor),
        ),
        onChanged: (value) => setState(() => searchQuery = value),
      ),
    );
  }

  Widget _buildWorkSitesList(List<WorkSiteResponse> sites, localizations, double sizeFactor) {
    if (isSelecting || widget.isLoading) {
      return Center(child: CircularProgressIndicator(color: Colors.grey[400], strokeWidth: 3 * sizeFactor));
    }
    return RefreshIndicator(
      onRefresh: () async => widget.onRefresh(),
      color: Colors.grey[400],
      child: ListView.builder(
        shrinkWrap: true,
        padding: EdgeInsets.all(24 * sizeFactor),
        itemCount: sites.length,
        itemBuilder: (context, index) {
          final site = sites[index];
          return _buildSiteTile(site, localizations, sizeFactor);
        },
      ),
    );
  }

  Widget _buildSiteTile(WorkSiteResponse site, localizations, double sizeFactor) {
    return Material(
      color: Colors.transparent,
      child: InkWell(
        borderRadius: BorderRadius.circular(16 * sizeFactor),
        onTap: isSelecting ? null : () async {
          setState(() => isSelecting = true);
          await Future.delayed(const Duration(milliseconds: 100));
          if (!mounted) return;
          widget.onSelect(site);
        },
        child: Container(
          padding: EdgeInsets.all(16 * sizeFactor),
          margin: EdgeInsets.symmetric(vertical: 4 * sizeFactor),
          decoration: BoxDecoration(
            color: const Color(0xFF2A2A2A),
            borderRadius: BorderRadius.circular(16 * sizeFactor),
            border: Border.all(color: const Color(0xFF363636)),
          ),
          child: Row(
            children: [
              _buildIcon(Icons.location_on_outlined, sizeFactor),
              SizedBox(width: 16 * sizeFactor),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(site.workSiteName ?? localizations.get('unnamedSite'), style: _textStyle(16 * sizeFactor, FontWeight.w500, Colors.grey[300])),
                    if (site.address != null)
                      Text(site.address!, style: _textStyle(14 * sizeFactor, FontWeight.w400, Colors.grey[500])),
                  ],
                ),
              ),
              Icon(Icons.chevron_right, color: Colors.grey[600], size: 20 * sizeFactor),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildIcon(IconData icon, double sizeFactor) => Container(
    padding: EdgeInsets.all(8 * sizeFactor),
    decoration: BoxDecoration(color: const Color(0xFF363636), borderRadius: BorderRadius.circular(12 * sizeFactor)),
    child: Icon(icon, color: Colors.grey[400], size: 20 * sizeFactor),
  );

  TextStyle _textStyle(double size, FontWeight weight, Color? color) => GoogleFonts.inter(fontSize: size, fontWeight: weight, color: color);
}
