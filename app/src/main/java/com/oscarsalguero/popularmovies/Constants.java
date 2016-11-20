/***
 * Copyright (c) 2016 Oscar Salguero www.oscarsalguero.com
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oscarsalguero.popularmovies;

/**
 * Constants class with Strings used across the app
 * Created by RacZo on 11/19/16.
 */
public abstract class Constants {

    public static final String API_SCHEME = "https";
    public static final String API_BASE_URL = "api.themoviedb.org";
    public static final String API_PATH_VERSION = "3";
    public static final String API_PATH_MOVIE = "movie";
    public static final String API_PARAM_API_KEY = "api_key";

    private static final String IMAGES_BASE_URL = "http://image.tmdb.org/t/p";
    // private static final String IMAGES_PATH_W185 = "/w185/";
    // private static final String IMAGES_PATH_W342 = "/w342/";
    private static final String IMAGES_PATH_W500 = "/w500/";

    public static final String IMAGES_URL = IMAGES_BASE_URL + IMAGES_PATH_W500;

    public static final String METHOD_GET = "GET";

}
