using UnityEngine;
using System.Collections;

public class CameraScript : MonoBehaviour {

    public float camera_v = 1;
    public float scroll_v = 1; 
    public Vector2 start_pos = new Vector2(0, 0);

	// Use this for initialization
	void Start () {
        gameObject.GetComponent<Transform>().position = start_pos;
	}
	
	// Update is called once per frame
	void FixedUpdate () {

        float x = camera_v * Input.GetAxis("Horizontal");
        float y = camera_v * Input.GetAxis("Vertical");
        float h = scroll_v * Input.GetAxis("Mouse ScrollWheel");
        gameObject.GetComponent<Transform>().Translate(x, y, h);

        
       


	}
}
