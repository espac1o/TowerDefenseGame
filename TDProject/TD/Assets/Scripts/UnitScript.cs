using UnityEngine;
using System.Collections.Generic;

public class UnitScript : MonoBehaviour {

    public float speed = 2;
    public float hp = 10;
    public float damage = 1f;

    public int reward = 1;

	private Dictionary<int, int[]> route; // general route to nexus, get it from GeneratorScript
	private int CELL_SIZE; // const size, get it from GeneratorScript

	private int currStep; // or how much it went (amount of cells)
	private const float CELL_PASSING_TIME = 32; // time that needs "to pass" the cell
	private float currPassingTime;
	private Vector3 distance;
	private bool isRotated;
    
    // Use this for initialization
	void Start () {
		var generator = GameObject.Find ("GENERATOR");
		route = generator.GetComponent<GeneratorScript> ().roadRoute;
		CELL_SIZE = generator.GetComponent<GeneratorScript> ().CELL_SIZE;
		currStep = 0;
		currPassingTime = CELL_PASSING_TIME;
		distance = new Vector3 (route[0][1] * CELL_SIZE, route[0][0] * CELL_SIZE, 0);
		isRotated = false;
	}
	
	// Update is called once per frame
	void FixedUpdate () {
		move ();
		//moveStep ();
	}

	void move() {
		int[] currCell = new int[2];
		int[] nextCell = new int[2];

		if (currPassingTime <= 0) {
			currPassingTime = CELL_PASSING_TIME;
			currStep++;
			isRotated = false;
			nextCell = route [currStep + 1];
			if (nextCell[0] < 0) {
				Vector3 currRotation = gameObject.GetComponent<Transform> ().eulerAngles;
				int currMode = (int)currRotation.z / 90 + 1; // f.e. if we have 90 gradus rotation, we will get mode 90/90 + 1 = 2
				if (nextCell [0] == -currMode)
					currMode = -nextCell [1];
				else
					currMode = -nextCell [0];
				currRotation.z = (currMode - 1) * 90;
				gameObject.GetComponent<Transform> ().eulerAngles = currRotation;
				isRotated = true;
			}
		}

		if (currStep + 1 >= route.Count)
			return;

		currCell = route [currStep];
		if (isRotated) {
			nextCell = route [currStep + 2];
		} else {
			nextCell = route [currStep + 1];
		}

		int x1 = currCell [1] * CELL_SIZE;
		int y1 = currCell [0] * CELL_SIZE;

		int x2 = nextCell [1] * CELL_SIZE;
		int y2 = nextCell [0] * CELL_SIZE;

		distance += new Vector3 (x2 - x1, y2 - y1, 0) * speed / CELL_PASSING_TIME;
		gameObject.GetComponent<Transform>().position = distance;

		currPassingTime -= speed;
	}

	void moveStep() {
		/*
			  2
			3   1
			  4
			  for rotation
		*/
		if (currStep > 0)
			return;
		int[] nextCell = new int[2];
		nextCell = route [currStep];

		distance = new Vector3 (nextCell [1] * CELL_SIZE, nextCell [0] * CELL_SIZE, 0);
		gameObject.GetComponent<Transform>().position = distance;
		currStep++;
	}

	public void lostHpOn(float damage) {
		hp -= damage;
		if (hp <= 0) {
			Destroy (gameObject);
		}
	}

    void OnDestroy()
    {
        GameObject.Find("COUNTER").GetComponent<CounterScript>().cash(reward);
        GameObject.Find("SPAWNER").GetComponent<SpawnerScript>().monsters_list.Remove(gameObject);
    }
}
