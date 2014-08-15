
package net.side5.httploader;

import net.side5.httploader.data.IndexesListData;
import net.side5.httploader.parameter.PostParameter;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HTTPLoader extends AsyncTask<Void, Integer, String> implements OnCancelListener , OnClickListener, OnKeyListener {
    public static enum TYPE {
        GET,
        POST,
        DOWNLOAD
    }

    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

    private Activity mActivity = null;
    private DefaultHttpClient mClient = null;
    private HttpPost mPostRequest = null;
    private HttpGet mGetRequest = null;
    private HttpResponse mResponse = null;
    private CookieManager mCookieManager = null;
    private List<Cookie> mCookies = null;

    private int mID = -1;
    private int mResultCode = -1;
    private int mContentLength = 0;
    private int mLoginPopupLayout = -1;
    private int mLoginPopupID = -1;
    private int mLoginPopupPW = -1;
    private int mLoginPopupAuto = -1;
    private String mURL = "";
    private String mResult = "";
    private String mOriginalFilename = "";
    private String mContentType = "";
    private String mCredentials = "";
    private String mDownloadPath = "";
    private String mDownloadFilename = "";
    private Object mObject = null;

    private byte[] mResultByteArray = null;

    private long mTotalSize = 0;
    private int mMultiProcessSize = 0;
    private int mProgressMessage = -1;

    private boolean mIsNonActivity = false;
    private boolean mIsRawOnly = false;
    private boolean mIsBackgroundProcessReturn = false;
    private boolean mIsAuthPopupDisplay = false;
    private boolean mIsIndexesArrayReturn = false;

    private TYPE mType = TYPE.GET;

    private Builder mBuilderItems = null;
    private NameValuePair mHeaderItem = null;
    private ArrayList<NameValuePair> mHeaderArray = null;
    private ArrayList<NameValuePair> mNameValuePostData = null;
    private ArrayList<PostParameter> mPostData = null;
    private ArrayList<MultiProcessData> mMultiProcessURL = null;
    private ArrayList<HTTPResult> mMultiProcessResultArray = null;
    private ArrayList<IndexesListData> mIndexesArray = null;
    private ProgressDialog mProgressDialog = null;
    private Dialog mLoadingDialog = null;
    private AlertDialog mAuthDialog = null;
    private OnCheckedChangeListener mCheckedChangeListener = null;

    public static class MultiProcessData {
        private int id = 0;
        private String url = "";
        public MultiProcessData(int id, String url) {
            this.id = id;
            this.url = url;
        }
        public MultiProcessData(String url) {
            this.id = -1;
            this.url = url;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Builder {
        private Activity builderActivity = null;
        private NameValuePair builderHeaderItem = null;
        private ArrayList<NameValuePair> builderHeaderArray = null;
        private ArrayList<NameValuePair> builderNameValuePostData = null;
        private ArrayList<PostParameter> builderPostData = null;
        private ArrayList<MultiProcessData> builderMultiProcessURL = null;
        private OnHTTPCallbackListener builderHTTPCallbackListener = null;
        private OnHTTPDownloadListener builderHTTPDownloadListener = null;
        private OnCheckedChangeListener builderCheckedChangeListener = null;
        private int builderID = -1;
        private int builderProgressMessage = -1;
        private int builderLoginPopupLayout = -1;
        private int builderLoginPopupID = -1;
        private int builderLoginPopupPW = -1;
        private int builderLoginPopupAuto = -1;
        private String builderCredentials = "";
        private String builderURL = "";
        private String builderDownloadPath = "";
        private String builderDownloadFilename = "";
        private Object builderObject = null;
        private int builderEnableLoading = -1;
        private int builderEnableProgress = -1;
        private boolean builderIsNonActivity = false;
        private boolean builderIsRawOnly = false;
        private boolean builderIsBackgroundProcessReturn = false;
        private boolean builderIsDebugMode = false;
        private boolean builderIsIndexesArrayReturn = false;
        private TYPE builderType = TYPE.GET;

        public Builder(Activity activity) {
            if (activity == null) {
                throw new IllegalArgumentException("Activity is null...");
            }

            this.builderActivity = activity;
        }

        public Builder(boolean isNonActivity) {
            this.builderIsNonActivity = isNonActivity;
        }

        public Builder setURL(String url) {
            this.builderURL = url;
            return this;
        }

        public Builder setDownloadURL(String downloadUrl) {
            this.builderURL = downloadUrl;
            this.builderType = TYPE.DOWNLOAD;
            return this;
        }

        public Builder enableProgress(int progressMessage) {
            if (builderActivity == null) {
                throw new IllegalArgumentException("Activity is null...");
            }

            this.builderEnableProgress = progressMessage;
            return this;
        }

        public Builder enableLoading(int loadingLayout) {
            if (builderActivity == null) {
                throw new IllegalArgumentException("Activity is null...");
            }

            this.builderEnableLoading = loadingLayout;
            return this;
        }

        public Builder setOnHTTPCallbackListener(OnHTTPCallbackListener l) {
            this.builderHTTPCallbackListener = l;
            return this;
        }

        public Builder setOnHTTPDownloadListener(OnHTTPDownloadListener l) {
            this.builderHTTPDownloadListener = l;
            return this;
        }

        public Builder setID(int id) {
            this.builderID = id;
            return this;
        }

        public Builder setHeader(ArrayList<NameValuePair> header) {
            this.builderHeaderArray = header;
            return this;
        }

        public Builder setHeader(NameValuePair header) {
            this.builderHeaderItem = header;
            return this;
        }

        public Builder setNameValuePostData(ArrayList<NameValuePair> nameValuePostData) {
            this.builderNameValuePostData = nameValuePostData;
            return this;
        }

        public Builder setPostData(ArrayList<PostParameter> post) {
            this.builderPostData = post;
            return this;
        }

        public Builder setMultiProcessURL(ArrayList<MultiProcessData> multiProcessURL) {
            this.builderMultiProcessURL = multiProcessURL;
            return this;
        }

        public Builder setType(TYPE type) {
            this.builderType = type;
            return this;
        }

        public Builder setProgressMessage(int message) {
            this.builderProgressMessage = message;
            return this;
        }

        public Builder setRawDataOnly(boolean rawOnly) {
            this.builderIsRawOnly = rawOnly;
            return this;
        }

        public Builder enableBackgroundProcessReturn() {
            this.builderIsBackgroundProcessReturn = true;
            return this;
        }

        public Builder setObject(Object object) {
            this.builderObject = object;
            return this;
        }

        public Builder setAuthorization(String id, String pw) {
            this.builderCredentials = Base64.encodeToString(new String(id + ":" + pw).getBytes(), Base64.NO_WRAP);
            return this;
        }

        public Builder setAuthorization(String credentials) {
            this.builderCredentials = credentials;
            return this;
        }

        public Builder setDownloadPath(String path) {
            this.builderDownloadPath = path;
            return this;
        }

        public Builder setDownloadFilename(String fileName) {
            this.builderDownloadFilename = fileName;
            return this;
        }

        public Builder enableDebug() {
            this.builderIsDebugMode = true;
            return this;
        }

        public Builder enableAuthPopup(int popupLayout, int popupId, int popupPw) {
            enableAuthPopup(popupLayout, popupId, popupPw, -1, null);
            return this;
        }

        public Builder enableAuthPopup(int popupLayout, int popupId, int popupPw, int popupAutoLogin, OnCheckedChangeListener autoLoginListener) {
            this.builderLoginPopupLayout = popupLayout;
            this.builderLoginPopupID = popupId;
            this.builderLoginPopupPW = popupPw;
            this.builderLoginPopupAuto = popupAutoLogin;
            this.builderCheckedChangeListener = autoLoginListener;
            return this;
        }

        public Builder enableIndexesArrayReturn() {
            this.builderIsIndexesArrayReturn = true;
            return this;
        }

        public HTTPLoader build() {
            return new HTTPLoader(this);
        }
    }

    public static class HTTPResult {
        private int id = -1;
        private int resultCode = 0;
        private int contentLength = 0;
        private String resultString = "";
        private String requestUrl = "";
        private String originalFileName = "";
        private String contentType = "";
        private String credentials = "";
        private Object object = null;
        private CookieManager cookieManager = null;
        private List<Cookie> cookie = null;
        private ArrayList<HTTPResult> resultArray = null;
        private ArrayList<IndexesListData> indexesArray = null;
        private byte[] rawData = null;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getResultString() {
            return resultString;
        }

        public void setResultString(String resultString) {
            this.resultString = resultString;
        }

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public CookieManager getCookieManager() {
            return cookieManager;
        }

        public void setCookieManager(CookieManager cookieManager) {
            this.cookieManager = cookieManager;
        }

        public List<Cookie> getCookie() {
            return cookie;
        }

        public void setCookie(List<Cookie> cookie) {
            this.cookie = cookie;
        }

        public ArrayList<HTTPResult> getResultArray() {
            return resultArray;
        }

        public void setResultArray(ArrayList<HTTPResult> resultArray) {
            this.resultArray = resultArray;
        }

        public String getRequestUrl() {
            return requestUrl;
        }

        public void setRequestUrl(String requestUrl) {
            this.requestUrl = requestUrl;
        }

        public int getContentLength() {
            return contentLength;
        }

        public void setContentLength(int contentLength) {
            this.contentLength = contentLength;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public void setOriginalFileName(String originalFileName) {
            this.originalFileName = originalFileName;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public byte[] getRawData() {
            return rawData;
        }

        public void setRawData(byte[] rawData) {
            this.rawData = rawData;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public String getCredentials() {
            return credentials;
        }

        public void setCredentials(String credentials) {
            this.credentials = credentials;
        }

        public ArrayList<IndexesListData> getIndexesArray() {
            return indexesArray;
        }

        public void setIndexesArray(ArrayList<IndexesListData> indexesArray) {
            this.indexesArray = indexesArray;
        }
    }

    public interface OnHTTPCallbackListener {
        public void onHTTPResult(HTTPResult result);
        public void onHTTPProcess(HTTPResult result);
        public void onHTTPError(HTTPResult result, Exception e);
        public void onHTTPStart();
        public void onHTTPComplete();
        public void onHTTPCancel();
    }

    public interface OnHTTPDownloadListener {
        public void onHTTPProgress(int current, int total);
        public void onHTTPDownloading(byte[] buffer);
        public void onHTTPDownloadComplete(String path, String filename);
    }

    private OnHTTPCallbackListener mOnHTTPCallbackListener = null;
    private OnHTTPDownloadListener mOnHTTPDownloadListener = null;

    public void setOnHTTPCallbackListener(OnHTTPCallbackListener l) {
        mOnHTTPCallbackListener = l;
    }

    public void setOnHTTPDownloadListener(OnHTTPDownloadListener l) {
        mOnHTTPDownloadListener = l;
    }

    public HTTPLoader() {}

    public HTTPLoader(boolean isNonActivity) {
        this.mIsNonActivity = isNonActivity;
    }

    public HTTPLoader(Activity activity) {
        this.mActivity = activity;
    }

    public HTTPLoader(Builder builder) {
        this.mIsNonActivity = builder.builderIsNonActivity;
        this.mActivity = builder.builderActivity;
        this.mURL = builder.builderURL;
        this.mID = builder.builderID;
        this.mHeaderItem = builder.builderHeaderItem;
        this.mHeaderArray = builder.builderHeaderArray;
        this.mNameValuePostData = builder.builderNameValuePostData;
        this.mPostData = builder.builderPostData;
        this.mMultiProcessURL = builder.builderMultiProcessURL;
        this.mType = builder.builderType;
        this.mProgressMessage = builder.builderProgressMessage;
        this.mIsRawOnly = builder.builderIsRawOnly;
        this.mObject = builder.builderObject;
        this.mIsBackgroundProcessReturn = builder.builderIsBackgroundProcessReturn;
        this.mOnHTTPCallbackListener = builder.builderHTTPCallbackListener;
        this.mOnHTTPDownloadListener = builder.builderHTTPDownloadListener;
        this.mCredentials = builder.builderCredentials;
        this.mDownloadPath = builder.builderDownloadPath;
        this.mDownloadFilename = builder.builderDownloadFilename;
        this.mLoginPopupLayout = builder.builderLoginPopupLayout;
        this.mLoginPopupID = builder.builderLoginPopupID;
        this.mLoginPopupPW = builder.builderLoginPopupPW;
        this.mLoginPopupAuto = builder.builderLoginPopupAuto;
        this.mCheckedChangeListener = builder.builderCheckedChangeListener;
        this.mIsIndexesArrayReturn = builder.builderIsIndexesArrayReturn;

        if (builder.builderMultiProcessURL !=null && (builder.builderEnableProgress != -1)) {
            enableProgress(builder.builderEnableProgress);
        }

        if (builder.builderEnableProgress != -1) {
            enableProgress(builder.builderEnableProgress);
        }

        if (builder.builderEnableLoading != -1) {
            enableLoading(builder.builderEnableLoading);
        }

        if (builder.builderIsDebugMode) {
            Trace.DEBUG_ENABLE = true;
        }

        if ((this.mLoginPopupLayout) != -1 && (this.mLoginPopupID != -1) && (this.mLoginPopupPW != -1)) {
            this.mIsAuthPopupDisplay = true;
        }

        mBuilderItems = builder;
    }

    public void setURL(String url) {
        mURL = url;
    }

    public String getURL() {
        return mURL;
    }

    public void enableProgress(int progressMessage) {
        mProgressMessage = progressMessage;

        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setCancelable(false);

        if (mMultiProcessURL != null) {
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        } else {
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        mProgressDialog.setMessage(mActivity.getResources().getString(mProgressMessage));
        mProgressDialog.setOnCancelListener(this);
    }

    public void enableLoading(int layout) {
        View loadingView = mActivity.getLayoutInflater().inflate(layout, null);

        mLoadingDialog = new Dialog(mActivity);
        mLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mLoadingDialog.setContentView(loadingView);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setOnCancelListener(this);
    }

    public void enableBackgroundProcessReturn() {
        mIsBackgroundProcessReturn = true;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void setID(int id) {
        mID = id;
    }

    public void setPostData(ArrayList<PostParameter> postData) {
        mPostData = postData;
    }

    public void setNameValuePostData(ArrayList<NameValuePair> nameValuePostData) {
        mNameValuePostData = nameValuePostData;
    }

    public void setHeader(ArrayList<NameValuePair> header) {
        mHeaderArray = header;
    }

    public void setHeader(NameValuePair header) {
        mHeaderItem = header;
    }

    public void setType(TYPE type) {
        mType = type;
    }

    public void setMultiProcessURL(ArrayList<MultiProcessData> multiProcessURL) {
        mMultiProcessURL = multiProcessURL;
    }

    public void setProgressMessage(int message) {
        mProgressMessage = message;

        if (mProgressDialog != null) {
            mProgressDialog.setMessage(mActivity.getResources().getString(message));
        }
    }

    public void setRawDataOnly(boolean rawOnly) {
        mIsRawOnly = rawOnly;
    }

    public void setObject(Object object) {
        mObject = object;
    }

    public void setAuthorization(String id, String pw) {
        mCredentials = Base64.encodeToString(new String(id + ":" + pw).getBytes(), Base64.NO_WRAP);
    }

    public void setAuthorization(String credentials) {
        mCredentials = credentials;
    }

    public void setDownloadPath(String path) {
        mDownloadPath = path;
    }

    public void setDownloadFilename(String fileName) {
        mDownloadFilename = fileName;
    }

    public static void enableDebug() {
        Trace.DEBUG_ENABLE = true;
    }

    public static void disableDebug() {
        Trace.DEBUG_ENABLE = false;
    }

    private void openAuthDialog() {
        if (mIsAuthPopupDisplay) {
            final View popupView = LayoutInflater.from(mActivity).inflate(mLoginPopupLayout, null);
            final EditText loginId = (EditText) popupView.findViewById(mLoginPopupID);
            final EditText loginPw = (EditText) popupView.findViewById(mLoginPopupPW);

            mAuthDialog = new AlertDialog.Builder(mActivity)
            .setView(popupView)
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mBuilderItems.setAuthorization(loginId.getText().toString(), loginPw.getText().toString());

                    HTTPLoader retryLoader = new HTTPLoader(mBuilderItems);
                    retryLoader.execute();
                }
            })
            .create();

            if (mLoginPopupAuto != -1) {
                final CheckBox autoLoginCheckbox = (CheckBox) popupView.findViewById(mLoginPopupAuto);
                autoLoginCheckbox.setOnCheckedChangeListener(mCheckedChangeListener);
            }

            mAuthDialog.show();
        }
    }

    private void procHeader() {
        if (mHeaderArray != null && mHeaderArray.size() > 0) {
            for (int i = 0; i < mHeaderArray.size(); i++) {
                if (mHeaderArray.get(i).getName().equals("Cookie")) {
                    CookieSyncManager.getInstance().sync();
                }

                mPostRequest.setHeader(mHeaderArray.get(i).getName(), mHeaderArray.get(i).getValue());
            }
        }

        if (mHeaderItem != null) {
            if (mHeaderItem.getName().equals("Cookie")) {
                CookieSyncManager.getInstance().sync();
            }

            mPostRequest.setHeader(mHeaderItem.getName(), mHeaderItem.getValue());
        }

        if (mCredentials != null && !mCredentials.isEmpty() && mGetRequest != null) {
            mGetRequest.setHeader("Authorization", String.format("Basic %s", mCredentials));
        }
    }

    private void procData() throws IOException {
        if (mPostData != null || mNameValuePostData != null || mType == TYPE.POST) {
            Trace.d("Request URL[POST] : " + mURL);
            mPostRequest = new HttpPost(mURL);

            procHeader();

            if (mPostData != null) {
                // for progress
                MultipartEntity formEntity = new MultipartEntity(HttpMultipartMode.STRICT);

                // We use FileBody to transfer an image
                for (PostParameter postParameter : mPostData) {
                    Trace.d( postParameter.getType()
                            + "  " + postParameter.getName()
                            + " : " + postParameter.getValue()
                            + (postParameter.getMimeType() != null
                                ? " [MimeType : " + postParameter.getMimeType()+"]"
                                        : "")
                            );
                    if (postParameter.getType() == PostParameter.TYPE_FILE) {
                        if (!postParameter.getValue().isEmpty()) {
                            File file = new File (postParameter.getValue());
                            FileBody fileBody = new FileBody(file, postParameter.getMimeType());
                            formEntity.addPart(postParameter.getName(), fileBody);
                        }
                    } else {
                        formEntity.addPart(postParameter.getName(), new StringBody(postParameter.getValue(), "text/plain", Charset.forName("UTF-8")));
                    }
                }
                mTotalSize = formEntity.getContentLength();
                mPostRequest.setEntity(formEntity);
            }

            if (mNameValuePostData != null) {
                mPostRequest.setEntity(new UrlEncodedFormEntity(mNameValuePostData, HTTP.UTF_8));
            }

            mResponse = mClient.execute(mPostRequest);
        } else {
            Trace.d("Request URL[GET] : " + mURL);
            mGetRequest = new HttpGet(mURL);

            procHeader();

            mResponse = mClient.execute(mGetRequest);
        }
    }

    private void procIndexesData(String result, ArrayList<IndexesListData> array) throws XPatherException {
        array.clear();

        IndexesListData data = null;

        String title = result;
        title = title.substring(title.indexOf("id=\"indextitle\""), title.indexOf("</h1>"));
        title = title.replace("id=\"indextitle\">Index of ", "");

        Trace.v("Title : " + title);

        CleanerProperties props = new CleanerProperties();
        props.setOmitUnknownTags(true);
        props.setOmitComments(true);
        props.setOmitDeprecatedTags(true);
        props.setOmitDoctypeDeclaration(true);
        props.setOmitHtmlEnvelope(true);
        props.setOmitXmlDeclaration(true);
        props.setAdvancedXmlEscape(true);
        props.setNamespacesAware(true);
        props.setTreatUnknownTagsAsContent(true);

        HtmlCleaner cleaner = new HtmlCleaner(props);
        TagNode node = cleaner.clean(result.trim());

        Object[] objArray = node.evaluateXPath("//table[@id='indexlist']");

        for (Object obj : objArray) {
            TagNode[] tr = ((TagNode) obj).getElementsByName("tr", true);
            for (int i = 0; i < tr.length; i++) {
                String trClass = tr[i].getAttributeByName("class");
                if (trClass.equals("even") || trClass.equals("odd")) {
                    data = new IndexesListData();
                    data.setTitle(title);

                    TagNode[] td = tr[i].getElementsByName("td", true);
                    for (int tdCount = 0; tdCount < td.length; tdCount++) {
                        String tdClass = td[tdCount].getAttributeByName("class");
                        if (!tdClass.equals("indexcolicon")) {
                            if (tdClass.equals("indexcolname")) {
                                TagNode[] a = td[tdCount].getElementsByName("a", true);
                                String href = a[0].getAttributeByName("href");
                                String name = a[0].getText().toString();

                                if (name.contains("/")) {
                                    data.setDir(true);
                                    name = name.substring(0, name.length() - 1);
                                }

                                data.setName(name.trim());
                                if (i == 1) {
                                    data.setUrl(String.format("%s", href));
                                } else {
                                    data.setUrl(String.format("%s/%s", title, href));
                                }

                                Trace.v("Name : " + name + " / " + href);
                            } else if (tdClass.equals("indexcollastmod")) {
                                String lastModify = td[tdCount].getText().toString().trim();

                                data.setLastModify(lastModify);

                                Trace.v("Lastmodify : " + lastModify);
                            } else if (tdClass.equals("indexcolsize")) {
                                String size = td[tdCount].getText().toString().trim();

                                if (!size.equals("-")) {
                                    data.setSize(size);
                                }
                            }
                        }
                    }

                    array.add(data);
                }

            }
        }
    }

    @Override
    protected void onPreExecute() {

        if (mActivity == null && !mIsNonActivity) {
            throw new IllegalArgumentException("Activity is null!");
        }

        if (mOnHTTPCallbackListener != null) {
            mOnHTTPCallbackListener.onHTTPStart();
        }

        if (mMultiProcessURL != null && mMultiProcessResultArray == null) {
            mMultiProcessResultArray = new ArrayList<HTTPResult>();
        }

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF8");
        HttpConnectionParams.setConnectionTimeout(params, DEFAULT_HTTP_CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, DEFAULT_HTTP_READ_TIMEOUT);

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

        mClient = new DefaultHttpClient(conMgr, params);

        if (mActivity != null) {
            mCookieManager = CookieManager.getInstance();
            CookieSyncManager.createInstance(mActivity);
        }

        if (mMultiProcessURL != null) {
            mMultiProcessSize = mMultiProcessURL.size();

            if (mMultiProcessResultArray == null) {
                mMultiProcessResultArray = new ArrayList<HTTPResult>();
            }

            if (mProgressDialog != null) {
                if (mMultiProcessSize > 1) {
                    mProgressDialog.setMax(mMultiProcessSize - 1);
                } else {
                    mProgressDialog.setMax(mMultiProcessSize);
                }
            }
        } else {
            mMultiProcessSize = 1;
        }

        show();

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        for (int i = 0; i < mMultiProcessSize; i++) {
            try {
                if (!mIsNonActivity) {
                    if (NetworkUtil.CheckStatus(mActivity) == NetworkUtil.NETWORK_NONE) {
                        throw new NetworkErrorException();
                    }
                }

                if (mMultiProcessURL != null) {
                    mURL = mMultiProcessURL.get(i).getUrl();
                }

                procData();

                mResultCode = mResponse.getStatusLine().getStatusCode();
                switch (mResultCode) {
                    case HttpStatus.SC_OK:
                        break;
                    default:
                        throw new IOException("Unexpected Http status code " + mResultCode);
                }

                Header[] responseHeaders = mResponse.getAllHeaders();

                for (int j = 0; j < responseHeaders.length; j++) {
                    String name = responseHeaders[j].getName();
                    String value = responseHeaders[j].getValue();

                    if (name.equals("Content-Length")) {
                        // Content-Length
                        mContentLength = Integer.parseInt(value);
                    } else if (name.equals("Content-Type")) {
                        // Content-Type
                        mContentType = value;
                    } else if (name.equals("Content-Disposition")) {
                        // Content-Disposition
                        mOriginalFilename = value.substring(value.indexOf("=\"") + 2, value.lastIndexOf("\""));
                    }
                }

                HttpEntity entity = mResponse.getEntity();

                if (entity != null && mType != TYPE.DOWNLOAD) {
                    if (!mIsRawOnly) {
                        mResult = EntityUtils.toString(entity, "UTF8");
                        Trace.v("result : " + mResult);
                    } else {
                        mResultByteArray = EntityUtils.toByteArray(entity);
                    }
                }

                mCookies = mClient.getCookieStore().getCookies();

                if (mCookieManager != null) {
                    mCookieManager.removeSessionCookie();
                }

                if (mIsBackgroundProcessReturn) {
                    HTTPResult resultData = new HTTPResult();

                    if (mMultiProcessURL != null && mMultiProcessURL.get(i).getId() != -1) {
                        resultData.setId(mMultiProcessURL.get(i).getId());
                    } else {
                        resultData.setId(mID);
                    }

                    resultData.setResultString(mResult);
                    resultData.setResultCode(mResultCode);
                    resultData.setRequestUrl(mURL);
                    resultData.setContentLength(mContentLength);
                    resultData.setContentType(mContentType);
                    resultData.setOriginalFileName(mOriginalFilename);
                    resultData.setRawData(mResultByteArray);
                    resultData.setObject(mObject);
                    resultData.setCredentials(mCredentials);
                    resultData.setIndexesArray(mIndexesArray);

                    if (mCookies != null) {
                        resultData.setCookie(mCookies);
                    }

                    if (mCookieManager != null) {
                        resultData.setCookieManager(mCookieManager);
                    }

                    if (!mIsBackgroundProcessReturn) {
                        mMultiProcessResultArray.add(resultData);
                    } else if (mIsBackgroundProcessReturn) {
                        if (mOnHTTPCallbackListener != null) {
                            mOnHTTPCallbackListener.onHTTPProcess(resultData);
                        }
                    }
                }

                if (mType == TYPE.DOWNLOAD) {
                    mDownloadFilename = URLDecoder.decode(mURL.substring(mURL.lastIndexOf("/") + 1, mURL.length()), "UTF-8");

                    URL url = new URL(mURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", String.format("Basic %s", mCredentials));
                    conn.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    File file = new File(mDownloadPath, mDownloadFilename);
                    FileOutputStream fileOutput = new FileOutputStream(file);
                    InputStream inputStream = conn.getInputStream();

                    int downloadedSize = 0;
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;

                        if (mOnHTTPDownloadListener != null) {
                            mOnHTTPDownloadListener.onHTTPProgress(downloadedSize, mContentLength);
                            mOnHTTPDownloadListener.onHTTPDownloading(buffer);
                        }
                    }

                    fileOutput.close();

                    if (mOnHTTPDownloadListener != null) {
                        mOnHTTPDownloadListener.onHTTPDownloadComplete(mDownloadPath, mDownloadFilename);
                    }
                }

                if (mIsIndexesArrayReturn) {
                    if (mIndexesArray == null) {
                        mIndexesArray = new ArrayList<IndexesListData>();
                    }

                    procIndexesData(mResult, mIndexesArray);
                }
            } catch (Exception e) {
                onCancelled();
                mResult = null;
                e.printStackTrace();
            }

            if (mMultiProcessURL != null) {
                publishProgress(i + 1);
            }
        }

        return mResult;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mProgressDialog != null) {
            mProgressDialog.setProgress(values[0]);
            mProgressDialog.setMessage(mActivity.getResources().getString(mProgressMessage));
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (dismiss()) {
                if (result == null) {
                    throw new Exception();
                }

                HTTPResult resultData = new HTTPResult();

                if (mMultiProcessResultArray != null) {
                    resultData.setId(mID);
                    resultData.setResultArray(mMultiProcessResultArray);
                } else {
                    resultData.setId(mID);
                    resultData.setResultString(result);
                    resultData.setResultCode(mResultCode);
                    resultData.setContentLength(mContentLength);
                    resultData.setContentType(mContentType);
                    resultData.setOriginalFileName(mOriginalFilename);
                    resultData.setRawData(mResultByteArray);
                    resultData.setObject(mObject);
                    resultData.setCredentials(mCredentials);
                    resultData.setIndexesArray(mIndexesArray);

                    if (mCookies != null) {
                        resultData.setCookie(mCookies);
                    }

                    if (mCookieManager != null) {
                        resultData.setCookieManager(mCookieManager);
                    }
                }

                //if (!mIsBackgroundProcessReturn) {
                    if (mOnHTTPCallbackListener != null) {
                        mOnHTTPCallbackListener.onHTTPResult(resultData);
                    }
                //}

                if (mOnHTTPCallbackListener != null) {
                    mOnHTTPCallbackListener.onHTTPComplete();
                }
            }
        } catch (Exception e) {
            if (mResultCode == HttpStatus.SC_UNAUTHORIZED) {
                openAuthDialog();
            }

            if (mOnHTTPCallbackListener != null) {
                HTTPResult resultData = new HTTPResult();
                resultData.setId(mID);
                resultData.setObject(mObject);
                resultData.setResultCode(mResultCode);
                mOnHTTPCallbackListener.onHTTPError(resultData, e);
            }
        }

        super.onPostExecute(result);
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public Object getObject() {
        return mObject;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        cancelHTTP();
    }

    @Override
    public void onClick(View v) {
        cancelHTTP();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            cancelHTTP();
        }
        return false;
    }

    public boolean cancelHTTP(int proposalId) {
        int id = (Integer)mObject;
        if (id == proposalId) {
            cancelHTTP();
            return true;
        } else {
            return false;
        }
    }

    public void cancelHTTP() {
        cancel(true);
        if (mPostRequest != null) {
            mPostRequest.abort();
        }

        if (mGetRequest != null) {
            mGetRequest.abort();
        }

        if (mOnHTTPCallbackListener != null) {
            mOnHTTPCallbackListener.onHTTPCancel();
        }

        dismiss();
    }

    private boolean dismiss() {
        if (!mIsNonActivity) {
            if (mActivity != null && !mActivity.isFinishing()) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }

                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void show() {
        if (!mIsNonActivity) {
            if (mActivity != null && !mActivity.isFinishing()) {
                if (mProgressDialog != null) {
                    mProgressDialog.show();
                } else if (mLoadingDialog != null) {
                    mLoadingDialog.show();
                }
            } else {
                cancelHTTP();
            }
        }
    }

}
