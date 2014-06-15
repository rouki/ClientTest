/**
 * Created with IntelliJ IDEA.
 * User: roee
 * Date: 6/13/14
 * Time: 12:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class PackageDetails {

    private String _to;
    private double _lat;
    private double _lng;
    private String[] _file_list;

    PackageDetails(String _to, double _lat, double _lng, String[] _file_list) {
        this._to = _to;
        this._lat = _lat;
        this._lng = _lng;
        this._file_list = _file_list;
    }

    String to() {
        return _to;
    }

    double lat() {
        return _lat;
    }

    double lng() {
        return _lng;
    }

    String[] fileList() {
        return _file_list;
    }
}
