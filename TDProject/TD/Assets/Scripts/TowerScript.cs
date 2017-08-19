using UnityEngine;
using System.Collections;

public class TowerScript : MonoBehaviour {

    public int[] cost = {1, 2, 3};
    public Sprite[] look;
    public GameObject[] ammo;

    public float[] shootingRate;
    public float[] damage;
    public float[] velocity;
    public float[] shootingRange;

	private int CELL_SIZE;

    public float Damage
    {
        get
        {
            return damage[lvl];
        }
    }
    public float Range
    {
        get
        {
            return shootingRange[lvl];
        }
    }
    public float Rate
    {
        get
        {
            return shootingRate[lvl];
        }
    }
    public float Cost
    {
        get
        {
            if (lvl + 1 >= cost.Length)
                return -1;
            return cost[lvl + 1];
        }
    }
    public Sprite Look
    {
        get
        {
            return look[lvl];
        }
    }

    public GameObject target = null;
    private int lvl;
    public int LVL
    {
        get
        {
            return lvl;
        }
        set
        {
            lvl = value;
        }
    }
    private float currentShotDelay;


    void Start () {
		CELL_SIZE = GameObject.Find ("GENERATOR").GetComponent<GeneratorScript> ().CELL_SIZE;
		gameObject.GetComponent<SpriteRenderer>().sprite = look[lvl];
        currentShotDelay = 0;
		lvl = 0;
        target = null;
		SetCircleCollider2DRadius (shootingRange [lvl]);
	}
	
	void FixedUpdate () {
        //shoot only if there is target and we are ready
        if (currentShotDelay > 0)
        {
            currentShotDelay -= Time.deltaTime;
        } else if(target)
        {
            
            Shot();
            currentShotDelay = shootingRate[lvl];
        }

        if (!target)
        {
            Aim();
        }
        else if (DistanceTo(target) > shootingRange[lvl] * CELL_SIZE)
        {
            target = null;
        }
        else { RotateToTarget(); }   	
	}

    public bool lvlUp()
    {
        if (lvl + 1 >= cost.Length) return false;
        if(!GameObject.Find("COUNTER").GetComponent<CounterScript>().Cash(-cost[lvl + 1])) return false;
        
        lvl++;
        gameObject.GetComponent<SpriteRenderer>().sprite = look[lvl];
        var ray = gameObject.transform.Find("ray");
        if (ray)
        {
            var AS = ray.GetComponent<AoeScript>();
            if (AS != null)
                AS.OnUpgrade();
        }
        return true;
      
    }

    void Shot(){
        if (ammo.Length == 0) return;
        var shot = Instantiate(ammo[lvl]) as GameObject;  // stop death before adding target
        AmmoScript AS = shot.GetComponent<AmmoScript>();
        shot.GetComponent<Transform>().position = gameObject.GetComponent<Transform>().position;
        AS.Damage = damage[lvl];
        AS.Speed = velocity[lvl];
        AS.Target = target;
        AS.TargetAssigned = true;

    }

    void Aim()
    {
        var spwn = GameObject.Find("SPAWNER");
        if (spwn == null)
        {
            return;
        }
        ArrayList monsters = spwn.GetComponent<SpawnerScript>().MonstersList;

        float minDistance = 1.01f * shootingRange[lvl] * CELL_SIZE;
        target = null;

        foreach(GameObject gameObj in monsters) {
            float d = DistanceTo(gameObj);
            if (d < minDistance)
            {
                minDistance = d;
                target = gameObj;
            }
        }
        
    }

    float DistanceTo(GameObject obj)
    {
        if (!obj)
        {
            return 10 * shootingRange[lvl] * CELL_SIZE;
        }
        Vector2 v_1 = obj.GetComponent<Transform>().position;
        Vector2 v_2 = gameObject.GetComponent<Transform>().position;

        float d = Mathf.Sqrt(Mathf.Pow(v_1.x - v_2.x, 2) + Mathf.Pow(v_1.y - v_2.y, 2));
        return d;        
    }

    void RotateToTarget() //rotate
    {
        float y1, y2;
        float x1, x2;
		float rotation;
		const float RAD_TO_GRAD_CONST = 180 / Mathf.PI;

		x1 = gameObject.GetComponent<Transform>().position.x;
		x2 = target.GetComponent<Transform>().position.x;
		y1 = gameObject.GetComponent<Transform>().position.y;
		y2 = target.GetComponent<Transform>().position.y;

		rotation = Mathf.Atan ((x1 - x2) / (y2 - y1)) * RAD_TO_GRAD_CONST;
		if (y2 - y1 < 0)
			rotation += 180;
		if (gameObject.GetComponent<Transform> ().rotation.z != rotation)
			gameObject.GetComponent<Transform> ().eulerAngles = new Vector3 (0, 0, rotation);
    }

	void SetCircleCollider2DRadius(float range) {
		gameObject.GetComponent<CircleCollider2D> ().radius = range * CELL_SIZE;
	}
}
