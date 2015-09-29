/**
 * 
 */
package br.com.skylane.voicer.camera;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
/**
 * @author maicon
 *
 */
@SuppressWarnings("deprecation")
public class CameraService {
	
	private static CameraService service;
	
	public static CameraService getInstance() {
		if (service == null)
			service = new CameraService();
		return service;
	}
	
	private Camera mCamera;	
		
	/**
	 * 
	 * @return
	 */
	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}
	
	/**
	 * 
	 * @return
	 */
	public Camera getFrontCamera() {
		if (mCamera == null)			
			mCamera = Camera.open(findFrontFacingCamera());
		return mCamera;
	}
	
	/**
	 * 
	 */
	public void releaseCamera() {
		// stop and release camera
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
    }
	
}
