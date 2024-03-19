package com.example.fyptommynorman;

import android.util.Log;

public class filterdAdvice {
    private String text;
    private String filter;

    public filterdAdvice(String text, String filter){
        this.filter = filter;
        this.text = text;
        Log.d("filterdAdvice", "new filterdAdvice object created: text=" + text + ", filter="+ filter);
    }

    public String getText(){
        return text;

    }

    public String getFilter(){
        return filter;
    }

    @Override
    public String toString(){
        return "filterdAdvice{" + "text='" + text + '\'' + ", filter='" + filter + '\'' + "}";
    }
}
