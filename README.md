# VistaRecyclerView
One approve `drop-down refresh` and bottom `load more` lightweight RecyclerView
## PreView

![preview1](/preview/vista_recycler_grid.gif)
![preview2](/preview/vista_recycler_linear.gif)

## Features

* customize the `Empty Layout`, `Error Layout`, and `Loading Progress Layout`
* achieved the drop-down refresh and bottom load more
* customize load more layout, including the loading, error and no more
* compatible LinearLayout, GridLayoutManager and StaggeredGridLayoutManager
* Lightweight,

## Usage

add the following code to your layout xml

    <me.shaohui.vistarecyclerview.VistaRecyclerView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/recycler"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

then, in the activity

    recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            fetchData();
        }
    });

    recyclerView.setOnMoreListener(new OnMoreListener() {
        @Override
        public void noMoreAsked(int total, int left, int current) {
            loadNoMore();
        }
    });

one more thing, to notify the dataSet change you have change your code from

    mAdapter.notifyDataSetChanged()

to
    mRecycler.notifyDataSetChanged()

or
    mRecycler.getAdapter().notifyDataSetChanged()

There also are some methods you might use

    mRecycler.showEmptyView() // if you want to show the empty view
    mRecycler.showErrorView() // if you fetch data catch some Exception, then show the error view

that is ALL!

Of course, you can also not add the above Listener, this is an ordinary recyclerView

there also have some Custom Attributes you can use

    app:refresh_color="@color/colorAccent"
    app:bottom_load_progress="@layout/bottom_layout"
    app:bottom_load_failure="@layout/bottom_load_failure"
    app:bottom_load_no_more="@layout/bottom_load_no_more"
    app:empty_layout="@layout/empty_layout"
    app:load_layout="@layout/load_layout"
    app:error_layout="@layout/error_layout"

## Import

Maven

    <dependency>
      <groupId>me.shaohui.vistarecyclerview</groupId>
      <artifactId>vistarecyclerview</artifactId>
      <version>1.0.3</version>
      <type>pom</type>
    </dependency>

    
Gradle

	dependencies {
        compile 'me.shaohui.vistarecyclerview:vistarecyclerview:1.0.3'
     }

## TODO


## License

    Copyright 2016 shaohui10086

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
	
 
 
