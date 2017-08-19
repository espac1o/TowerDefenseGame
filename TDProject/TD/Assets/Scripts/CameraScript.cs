using UnityEngine;
using System.Collections;

public class CameraScript : MonoBehaviour {

    public float camera_v = 1;
    public float scroll_v = 1; 
    public Vector3 start_pos = new Vector3(0, 0, 0);

	// Use this for initialization
	void Start () {
        gameObject.GetComponent<Transform>().position = start_pos;
	}
	
	// Update is called once per frame
	void Update () {
		float timeVelocity;
		if (Time.timeScale == 0)
			timeVelocity = 1;
		else
			timeVelocity = 1 / Time.timeScale;

		float x = camera_v * Input.GetAxis("Horizontal") * timeVelocity;
		float y = camera_v * Input.GetAxis("Vertical") * timeVelocity;
		float z = scroll_v * Input.GetAxis("Mouse ScrollWheel") * timeVelocity;

        var generatorScript = GameObject.Find("GENERATOR").GetComponent<GeneratorScript>();
        Vector3 cameraPos = gameObject.GetComponent<Transform>().position;

        x = CheckCamPos(cameraPos.x, 0, generatorScript.map_size_x * generatorScript.CELL_SIZE, x);
        y = CheckCamPos(cameraPos.y, -generatorScript.map_size_y * generatorScript.CELL_SIZE, 0, y);
        z = CheckCamPos(cameraPos.z, -5, 0, z);
        gameObject.GetComponent<Transform>().Translate(x, y, z);
	}

    private float CheckCamPos(float component, float min, float max, float delta)
    {
        if (component + delta > max)
        {
            delta = max - component;
        }
        if (component + delta < min)
        {
            delta = min - component;
        }
        if (component < min || component > max)
        {
            gameObject.GetComponent<Transform>().position = start_pos;
        }
        return delta;
    }
}
