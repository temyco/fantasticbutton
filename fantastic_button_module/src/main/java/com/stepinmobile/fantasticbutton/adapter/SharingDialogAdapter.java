/**
 * Copyright 2014-present StepInMobile.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.stepinmobile.fantasticbutton.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.stepinmobile.fantasticbutton.R;
import com.stepinmobile.fantasticbutton.objects.SharingDlgItem;

import java.util.List;

/**
 * This adapter used for displaying sharing items in dialogue.
 *
 * Created by Nastya on 24.09.2014.
 */
public class SharingDialogAdapter extends ArrayAdapter<SharingDlgItem> {
    private List<SharingDlgItem> items;
    private AQuery aq;

    public SharingDialogAdapter(Context context, int resource, List<SharingDlgItem> objects) {
        super(context, resource, objects);

        this.items = objects;
        this.aq = new AQuery(context);
    }

    @Override
    public int getPosition(SharingDlgItem item) {
        return super.getPosition(item);
    }

    @Override
    public SharingDlgItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).stringId;
    }

    @Override
    public int getCount() {
        if(this.items != null)
            return this.items.size();

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = aq.inflate(convertView, R.layout.dlg_item, parent);
            aq.recycle(convertView);
            holder = new ViewHolder();
            holder.icon = aq.id(R.id.ic_social_icon).getImageView();
            holder.name = aq.id(R.id.txt_social_name).getTextView();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(this.items != null) {
            aq.id(holder.icon).image(this.items.get(position).iconId);
            aq.id(holder.name).text(this.items.get(position).stringId);
        }

        return convertView;
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView name;
    }
}
