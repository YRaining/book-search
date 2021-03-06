//
// Created by Jhon on 2016/1/2.
//
#include "com_example_jhon_opencv_OpenCVHelper.h"
#include <stdio.h>
#include <stdlib.h>
#include <opencv2/opencv.hpp>
#include <vector>
#include <fstream>
#include <sstream>
using namespace cv;
using namespace std;
extern "C" {

JNIEXPORT jstring JNICALL Java_com_example_jhon_opencv_OpenCVHelper_gray(
        JNIEnv *env, jclass obj, jintArray buf, int w, int h);



JNIEXPORT jstring JNICALL Java_com_example_jhon_opencv_OpenCVHelper_gray(
        JNIEnv *env, jclass obj, jintArray buf, int w, int h) {

    const float nn_match_ratio = 0.8f;

    jint *cbuf,*dbuf;
    cbuf=env->GetIntArrayElements(buf,(jboolean*) false);
    if(cbuf == NULL) {
        return 0;
    }

    Mat srcImg(h, w, CV_8UC4, (unsigned char*)cbuf);
    Mat img2(h, w, CV_8UC1);
    cvtColor(srcImg, img2, COLOR_BGR2GRAY);
    //resize(img2, img2, Size(), 0.19, 0.19, CV_INTER_AREA);

    vector<KeyPoint> kp1, kp2;
    Mat des1, des2;

    Ptr<AKAZE> akaze = AKAZE::create();
    akaze->detectAndCompute(img2, noArray(), kp2, des2);

    int Maxinliers_num = 0;
    string matchbook;
    vector < DMatch> bestmatches;
    Mat bestmask;

    string s;
    ifstream booknumfile("/storage/sdcard1/bookData/kp&des/num_info.txt");
    //ifstream booknumfile("/storage/0EF3-2A19/bookData/kp&des/num_info.txt");
    getline(booknumfile, s);
    booknumfile.close();
    int book_num = atoi(s.c_str());

     for (int i = 0; i <book_num; i++)
     {
         stringstream ss;
         ss<<i;
         FileStorage fs_r("/storage/sdcard1/bookData/kp&des/"+ss.str()+".xml", FileStorage::READ);
        // FileStorage fs_r("/storage/0EF3-2A19/bookData/kp&des/"+ss.str()+".xml", FileStorage::READ);
         //FileStorage fs_r("bookData/kp&des/"+to_string(i)+".xml", FileStorage::READ);
         fs_r["kp"] >> kp1;
         fs_r["des"] >> des1;
         fs_r.release();

         BFMatcher matcher(NORM_HAMMING);
         vector<vector<DMatch>> nn_matches;
         matcher.knnMatch(des1, des2, nn_matches, 2);

         vector<DMatch> good_matches;
         for (size_t i = 0; i < nn_matches.size(); i++){
             DMatch first = nn_matches[i][0];
             float dist1 = nn_matches[i][0].distance;
             float dist2 = nn_matches[i][1].distance;

             if (dist1 < nn_match_ratio*dist2){
                 good_matches.push_back(first);
             }
         }

        vector<Point2f> pt1, pt2;
         Mat mask;
         for (int i = 0; i < good_matches.size(); i++){
             pt1.push_back(kp1[good_matches[i].queryIdx].pt);
             pt2.push_back(kp2[good_matches[i].trainIdx].pt);
         }
         findHomography(pt1, pt2, RANSAC, 3, mask);

         int inliers_num = 0;
         for (int i = 0; i<mask.size().height; i++){
             inliers_num += (uint)mask.at<uchar>(0, i);
         }
         if (inliers_num >= Maxinliers_num){
             Maxinliers_num = inliers_num;
             //matchbook = to_string(i);
             stringstream kk;
             kk<<i;
             matchbook=kk.str();
         }
     }
    if(Maxinliers_num<50){
        matchbook="无匹配结果";
    }
    //ifstream bookinfofile("/storage/sdcard1/bookData/info/"+matchbook+".txt");
    //ifstream bookinfofile("/storage/0EF3-2A19/bookData/info/"+matchbook+".txt");

    //getline(bookinfofile, matchbook);
   // booknumfile.close();

     jstring  result=env->NewStringUTF((const char*)matchbook.c_str());
     return result;
}
}
