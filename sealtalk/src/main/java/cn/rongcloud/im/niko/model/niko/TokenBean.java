package cn.rongcloud.im.niko.model.niko;

public class TokenBean {

    /**
     * access_token : eyJhbGciOiJSUzI1NiIsImtpZCI6IjkzZjZmNWNkYzkwM2I2MTQ0MzczNDIwNzc0ODMxZDI5IiwidHlwIjoiSldUIn0.eyJuYmYiOjE1OTQ3MTAwOTcsImV4cCI6MTU5NTMxMDA5NywiaXNzIjoiaHR0cHM6Ly90dGlkLmFsaWx1c2lvbnMuY29tIiwiYXVkIjpbImh0dHBzOi8vdHRpZC5hbGlsdXNpb25zLmNvbS9yZXNvdXJjZXMiLCJqakFwaVNjb3BlIl0sImNsaWVudF9pZCI6ImpqQXBwQXBpQ2xpZW50Iiwic2NvcGUiOlsiampBcGlTY29wZSJdfQ.gV14iMnzBSyD8Aev99l_ANzwT_SFOi6hMdgdm_oYSn_fya7w43eAcJk_FLYBAt9VZyUDLNj921ilEoSPoyxh9WgKJGWhiEKfbZEQbOE1rkcILTkojgX9Q2g9sYrdI-6jERb9223O3cDT2NcbleEzD4808Z5nVwVArMje33u53rw2rDR3OfG_n6ra3jwtHWYFcoY3b4rcWd3stLSUYwot6qkVZyXRV-4cHkOH820pxSZBwC9XcsGl6ZLaukFwXMLnAW2z_EpSPFRzLLpwGGit8FL-Wa26iOxnpt-W7XIjdQY24plk7YA7WWZ-2MlfoMDi3Pxiz5qZBNR5KgOvjDrq4w
     * expires_in : 600000
     * token_type : Bearer
     */

    private String access_token;
    private int expires_in;
    private String token_type;
    private String error;
    private int UID;

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}
