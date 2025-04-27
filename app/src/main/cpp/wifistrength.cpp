// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("wifistrength");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("wifistrength")
//      }
//    }

#include <jni.h>
#include <string>
#include <sstream>
#include <vector>
//#include <Eigen/Dense>

using namespace std;
//using namespace Eigen;

//extern "C" JNIEXPORT jstring JNICALL
//Java_com_arorashivoy_wifistrength_Screen1Activity_matrixOperation(
//        JNIEnv* env,
//        jobject /* this */,
//        jstring operation,
//        jint dim,
//        jfloatArray matrixA,
//        jfloatArray matrixB) {
//
//    int n = dim;
//    const char *op = env->GetStringUTFChars(operation, nullptr);
//
//    jfloat* a = env->GetFloatArrayElements(matrixA, nullptr);
//    jfloat* b = env->GetFloatArrayElements(matrixB, nullptr);
//
//    MatrixXf matA(n, n);
//    MatrixXf matB(n, n);
//
//    for (int i = 0; i < n*n; i++) {
//        matA(i/n, i%n) = a[i];
//        matB(i/n, i%n) = b[i];
//    }
//
//    MatrixXf result(n, n);
//
//    if (strcmp(op, "add") == 0) {
//        result = matA + matB;
//    } else if (strcmp(op, "sub") == 0) {
//        result = matA - matB;
//    } else if (strcmp(op, "mul") == 0) {
//        result = matA * matB;
//    } else if (strcmp(op, "div") == 0) {
//        result = matA * matB.inverse();
//    }
//
//    ostringstream oss;
//    for (int i = 0; i < n; i++) {
//        for (int j = 0; j < n; j++) {
//            oss << result(i, j) << " ";
//        }
//        oss << "\n";
//    }
//
//    env->ReleaseFloatArrayElements(matrixA, a, 0);
//    env->ReleaseFloatArrayElements(matrixB, b, 0);
//    env->ReleaseStringUTFChars(operation, op);
//
//    return env->NewStringUTF(oss.str().c_str());
//}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_arorashivoy_wifistrength_Screen1Activity_matrixOperation(JNIEnv *env, jobject thiz,
                                                                  jstring op, jintArray dim,
                                                                  jfloatArray matrix_a,
                                                                  jfloatArray matrix_b) {
    // TODO: implement matrixOperation()
    jboolean isCopy;
    const char* operation = env->GetStringUTFChars(op, &isCopy);

    jint* dimensions = env->GetIntArrayElements(dim, nullptr);
    int rowsA = dimensions[0];
    int colsA = dimensions[1];
    int rowsB = dimensions[2];
    int colsB = dimensions[3];

    int sizeA = rowsA * colsA;
    int sizeB = rowsB * colsB;

    jfloat* matA = env->GetFloatArrayElements(matrix_a, nullptr);
    jfloat* matB = env->GetFloatArrayElements(matrix_b, nullptr);

    vector<vector<float>> A(rowsA, vector<float>(colsA));
    vector<vector<float>> B(rowsB, vector<float>(colsB));
    int index = 0;
    for (int i = 0; i < rowsA; ++i) {
        for (int j = 0; j < colsA; ++j) {
            A[i][j] = matA[index++];
        }
    }
    index = 0;
    for (int i = 0; i < rowsB; ++i) {
        for (int j = 0; j < colsB; ++j) {
            B[i][j] = matB[index++];
        }
    }

    vector<vector<float>> result;
    ostringstream oss;

    if (strcmp(operation, "add") == 0) {
        if (rowsA != rowsB || colsA != colsB) {
            oss << "Dimension mismatch for addition.";
        } else {
            result.resize(rowsA, vector<float>(colsA, 0));
            for (int i = 0; i < rowsA; ++i) {
                for (int j = 0; j < colsA; ++j) {
                    result[i][j] = A[i][j] + B[i][j];
                }
            }
        }
    } else if (strcmp(operation, "subtract") == 0) {
        if (rowsA != rowsB || colsA != colsB) {
            oss << "Dimension mismatch for subtraction.";
        } else {
            result.resize(rowsA, vector<float>(colsA, 0));
            for (int i = 0; i < rowsA; ++i) {
                for (int j = 0; j < colsA; ++j) {
                    result[i][j] = A[i][j] - B[i][j];
                }
            }
        }
    } else if (strcmp(operation, "multiply") == 0) {
        if (colsA != rowsB) {
            oss << "Dimension mismatch for multiplication.";
        } else {
            result.resize(rowsA, vector<float>(colsB, 0));
            for (int i = 0; i < rowsA; ++i) {
                for (int j = 0; j < colsB; ++j) {
                    for (int k = 0; k < colsA; ++k) {
                        result[i][j] += A[i][k] * B[k][j];
                    }
                }
            }
        }
    } else if (strcmp(operation, "divide") == 0) {
        if (rowsA != rowsB || colsA != colsB) {
            oss << "Dimension mismatch for division.";
        } else {
            result.resize(rowsA, vector<float>(colsA, 0));
            for (int i = 0; i < rowsA; ++i) {
                for (int j = 0; j < colsA; ++j) {
                    if (B[i][j] == 0.0f) {
                        oss << "Division by zero at (" << i << "," << j << ")";
                        goto release;
                    }
                    result[i][j] = A[i][j] / B[i][j];
                }
            }
        }
    } else {
        oss << "Unknown operation: " << operation;
    }

    if (oss.str().empty()) {
        for (const auto& row : result) {
            for (float val : row) {
                oss << val << " ";
            }
            oss << "\n";
        }
    }

    release:
    env->ReleaseFloatArrayElements(matrix_a, matA, 0);
    env->ReleaseFloatArrayElements(matrix_b, matB, 0);
    env->ReleaseIntArrayElements(dim, dimensions, 0);
    env->ReleaseStringUTFChars(op, operation);

    return env->NewStringUTF(oss.str().c_str());
}